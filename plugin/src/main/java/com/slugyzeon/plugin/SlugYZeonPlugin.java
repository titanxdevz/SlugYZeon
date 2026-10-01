package com.slugyzeon.plugin;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.slugyzeon.plugin.amazonmusic.AmazonMusicAudioSourceManager;
import com.slugyzeon.plugin.config.*;
import com.slugyzeon.plugin.gaana.GaanaAudioSourceManager;
import com.slugyzeon.plugin.pandora.PandoraAudioSourceManager;
import com.slugyzeon.plugin.spotify.SpotifyAudioSourceManager;
import com.slugyzeon.plugin.youtube.YouTubeSourceManager;
import dev.arbjerg.lavalink.api.AudioPlayerManagerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Service
@RestController
public class SlugYZeonPlugin implements AudioPlayerManagerConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SlugYZeonPlugin.class);
    private static final String[] DEFAULT_PROVIDERS = new String[] {
            "dzisrc:%ISRC%",
            "ytsearch:\"%ISRC%\"",
            "ytmsearch:%QUERY%",
            "ytsearch:%QUERY%"
    };

    private AudioPlayerManager manager;

    private GaanaAudioSourceManager gaana;
    private AmazonMusicAudioSourceManager amazonMusic;
    private PandoraAudioSourceManager pandora;
    private SpotifyAudioSourceManager spotify;
    private YouTubeSourceManager youtube;

    public SpotifyAudioSourceManager getSpotify() {
        return spotify;
    }

    public SlugYZeonPlugin(
            org.springframework.core.env.Environment env,
            SlugYZeonSourcesConfig sourcesConfig,
            GaanaConfig gaanaConfig,
            AmazonMusicConfig amazonMusicConfig,
            PandoraConfig pandoraConfig,
            SlugYZeonSpotifyConfig spotifyConfig,
            SlugYZeonYouTubeConfig youtubeConfig) {
        log.info("Loading SlugYZeoN plugin...");
        
        String[] slugyzeonProviders = env.getProperty("plugins.slugyzeon.providers", String[].class);
        
        String[] providersToUse = DEFAULT_PROVIDERS;
        if (slugyzeonProviders != null && slugyzeonProviders.length > 0) {
            providersToUse = slugyzeonProviders;
        }

        if (sourcesConfig.isGaana()) {
            this.gaana = new GaanaAudioSourceManager(
                    gaanaConfig.getApiUrl(),
                    gaanaConfig.getPlaylistLoadLimit(),
                    gaanaConfig.getAlbumLoadLimit(),
                    gaanaConfig.getArtistLoadLimit(),
                    unused -> manager);
        }
        if (sourcesConfig.isAmazonmusic()) {
            this.amazonMusic = new AmazonMusicAudioSourceManager(
                    providersToUse,
                    amazonMusicConfig.getApiUrl(),
                    amazonMusicConfig.getPlaylistLoadLimit(),
                    amazonMusicConfig.getAlbumLoadLimit(),
                    amazonMusicConfig.getArtistLoadLimit(),
                    unused -> manager);
        }
        if (sourcesConfig.isPandora()) {
            this.pandora = new PandoraAudioSourceManager(
                    providersToUse,
                    pandoraConfig.getCsrfToken(),
                    pandoraConfig.getSearchLimit(),
                    unused -> manager);
        }
        if (sourcesConfig.isSpotify()) {
            this.spotify = new SpotifyAudioSourceManager(
                    providersToUse,
                    spotifyConfig.getCountryCode(),
                    spotifyConfig.getPlaylistLoadLimit(),
                    spotifyConfig.getAlbumLoadLimit(),
                    spotifyConfig.isResolveArtistsInSearch(),
                    spotifyConfig.isLocalFiles(),
                    spotifyConfig.getSpDc(),
                    unused -> manager);
        }
        if (sourcesConfig.isYoutube()) {
            if (youtubeConfig.getApiUrl() != null && !youtubeConfig.getApiUrl().trim().isEmpty() &&
                youtubeConfig.getMasterKey() != null && !youtubeConfig.getMasterKey().trim().isEmpty()) {
                this.youtube = new YouTubeSourceManager(youtubeConfig.getApiUrl(), youtubeConfig.getMasterKey(), unused -> manager);
            }
        }
    }

    @Override
    public AudioPlayerManager configure(AudioPlayerManager manager) {
        this.manager = manager;

        if (gaana != null) {
            log.info("Registering Gaana audio source manager...");
            manager.registerSourceManager(gaana);
        }
        if (amazonMusic != null) {
            log.info("Registering Amazon Music audio source manager...");
            manager.registerSourceManager(amazonMusic);
        }
        if (pandora != null) {
            log.info("Registering Pandora audio source manager...");
            manager.registerSourceManager(pandora);
        }
        if (spotify != null) {
            log.info("Registering Spotify audio source manager...");
            manager.registerSourceManager(spotify);
        }

        return manager;
    }

    @org.springframework.context.event.EventListener(org.springframework.boot.context.event.ApplicationReadyEvent.class)
    public void onApplicationReady() {
        if (youtube != null && manager != null) {
            youtube.attachToYouTube(manager);
        }
    }

    @PatchMapping("/v4/slugyzeon/config")
    public void updateConfig(@RequestBody ConfigUpdate config) {
        if (config.getSpotify() != null && this.spotify != null) {
            if (config.getSpotify().getSpDc() != null) {
                this.spotify.setSpDc(config.getSpotify().getSpDc());
                log.info("Successfully live-updated Spotify spDc cookie.");
            }
        }
    }

    public static class ConfigUpdate {
        private SpotifyConfig spotify;

        public SpotifyConfig getSpotify() { return spotify; }
        public void setSpotify(SpotifyConfig spotify) { this.spotify = spotify; }

        public static class SpotifyConfig {
            private String spDc;
            public String getSpDc() { return spDc; }
            public void setSpDc(String spDc) { this.spDc = spDc; }
        }
    }
}