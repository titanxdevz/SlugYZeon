package com.slugyzeon.plugin.youtube;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.*;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YouTubeSourceManager implements AudioSourceManager {

    private static final Logger log = LoggerFactory.getLogger(YouTubeSourceManager.class);
    private final Function<Void, AudioPlayerManager> audioPlayerManager;
    private AudioSourceManager originalYouTubeSource;
    private final boolean oembed;
    private final boolean mirror;
    private final java.util.concurrent.ExecutorService networkExecutor = java.util.concurrent.Executors.newFixedThreadPool(10);
    private final java.net.http.HttpClient httpClient = java.net.http.HttpClient.newBuilder().followRedirects(java.net.http.HttpClient.Redirect.ALWAYS).connectTimeout(java.time.Duration.ofSeconds(10)).build();
    private final com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager httpSourceManager = new com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager();

    public com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager getHttpSourceManager() {
        return httpSourceManager;
    }

    public YouTubeSourceManager(
            boolean oembed,
            boolean mirror,
            Function<Void, AudioPlayerManager> audioPlayerManager) {
        this.oembed = oembed;
        this.mirror = mirror;
        this.audioPlayerManager = audioPlayerManager;
    }

    public Function<Void, AudioPlayerManager> getAudioPlayerManager() {
        return audioPlayerManager;
    }

    public AudioSourceManager getOriginalYouTubeSource() {
        return originalYouTubeSource;
    }

    @SuppressWarnings("unchecked")
    public boolean attachToYouTube(AudioPlayerManager manager) {
        List<AudioSourceManager> sources = findSourceList(manager);
        if (sources == null)
            return false;

        for (int i = 0; i < sources.size(); i++) {
            AudioSourceManager source = sources.get(i);
            if (source instanceof YouTubeSourceManager)
                continue;

            boolean isYouTube = "youtube".equalsIgnoreCase(source.getSourceName());
            if (!isYouTube) {
                String className = source.getClass().getName().toLowerCase();
                isYouTube = className.contains("youtube") || className.contains("youtubeaudiosource");
            }

            if (isYouTube) {
                this.originalYouTubeSource = source;
                sources.set(i, this);
                log.info("Attached SlugYZeon-YTCDN to YouTube source {}", source.getClass().getName());
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    private List<AudioSourceManager> findSourceList(AudioPlayerManager manager) {
        Class<?> clazz = manager.getClass();

        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(manager);
                    if (value instanceof List) {
                        List<?> list = (List<?>) value;
                        if (!list.isEmpty() && list.get(0) instanceof AudioSourceManager) {
                            return (List<AudioSourceManager>) value;
                        }
                        if (list.isEmpty() && field.getName().toLowerCase().contains("source")) {
                            return (List<AudioSourceManager>) value;
                        }
                    }
                } catch (Exception ignored) {
                }
            }
            clazz = clazz.getSuperclass();
        }

        return null;
    }

    @Override
    public String getSourceName() {
        return "youtube";
    }

    public java.util.concurrent.ExecutorService getNetworkExecutor() {
        return networkExecutor;
    }

    public java.net.http.HttpClient getHttpClient() {
        return httpClient;
    }

    @Override
    public AudioItem loadItem(AudioPlayerManager manager, AudioReference reference) {
        if (originalYouTubeSource == null)
            return null;

        AudioItem result = null;

        try {
            result = originalYouTubeSource.loadItem(manager, reference);
        } catch (Exception ignored) {
        }

        if (result != null) {
            if (result instanceof AudioTrack) {
                return wrapTrack((AudioTrack) result);
            }
            if (result instanceof AudioPlaylist) {
                AudioPlaylist original = (AudioPlaylist) result;
                List<AudioTrack> fixedTracks = new ArrayList<>();
                for (AudioTrack track : original.getTracks()) {
                    fixedTracks.add(wrapTrack(track));
                }
                AudioTrack selectedTrack = original.getSelectedTrack() != null
                        ? wrapTrack(original.getSelectedTrack())
                        : null;
                return new BasicAudioPlaylist(original.getName(), fixedTracks, selectedTrack, original.isSearchResult());
            }
            return result;
        }

        return null;
    }

    String checkCdnStreamUrl(String videoId) {
        try {
            var req = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(apiUrl + "/api/v1/metadata/" + videoId))
                .header("User-Agent", "SlugYZeon-Node")
                .timeout(java.time.Duration.ofSeconds(3))
                .GET().build();
            var res = httpClient.send(req, java.net.http.HttpResponse.BodyHandlers.ofString());
            
            if (res.statusCode() == 200 && res.body() != null) {
                return apiUrl + "/api/v1/stream/" + videoId;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private AudioTrack wrapTrack(AudioTrack original) {
        return new YouTubeTrack(original.getInfo(), original.getInfo().identifier, original, this);
    }

    @Override
    public boolean isTrackEncodable(AudioTrack track) {
        if (track instanceof YouTubeTrack) {
            AudioTrack original = ((YouTubeTrack) track).getOriginalTrack();
            if (original != null && originalYouTubeSource != null) {
                return originalYouTubeSource.isTrackEncodable(original);
            }
            return true;
        }
        return originalYouTubeSource != null ? originalYouTubeSource.isTrackEncodable(track) : true;
    }

    @Override
    public void encodeTrack(AudioTrack track, DataOutput output) throws IOException {
        if (track instanceof YouTubeTrack) {
            AudioTrack original = ((YouTubeTrack) track).getOriginalTrack();
            if (original != null && originalYouTubeSource != null) {
                output.writeBoolean(true);
                originalYouTubeSource.encodeTrack(original, output);
            } else {
                output.writeBoolean(false);
            }
        } else if (originalYouTubeSource != null) {
            output.writeBoolean(true);
            originalYouTubeSource.encodeTrack(track, output);
        } else {
            output.writeBoolean(false);
        }
    }

    @Override
    public AudioTrack decodeTrack(AudioTrackInfo trackInfo, DataInput input) throws IOException {
        AudioTrack original = null;
        try {
            boolean hasOriginal = input.readBoolean();
            if (hasOriginal && originalYouTubeSource != null) {
                original = originalYouTubeSource.decodeTrack(trackInfo, input);
            }
        } catch (Exception ignored) {
        }
        
        return new YouTubeTrack(trackInfo, trackInfo.identifier, original, this);
    }

    @Override
    public void shutdown() {
        if (originalYouTubeSource != null)
            originalYouTubeSource.shutdown();
        networkExecutor.shutdownNow();
    }
}