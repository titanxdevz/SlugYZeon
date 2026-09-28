package com.slugyzeon.plugin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.slugyzeon.plugin.spotify.SpotifyAudioSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v4/spotify")
public class SpotifyLyricsController {

    private static final Logger log = LoggerFactory.getLogger(SpotifyLyricsController.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final SlugYZeonPlugin plugin;

    public SpotifyLyricsController(SlugYZeonPlugin plugin) {
        this.plugin = plugin;
    }

    @GetMapping("/color-lyrics")
    public ResponseEntity<String> getColorLyrics(
            @RequestParam(required = false) String trackId,
            @RequestParam(required = false) String isrc,
            @RequestParam(required = false) String artworkUrl) {
        try {
            SpotifyAudioSourceManager spotify = plugin.getSpotify();
            if (spotify == null) {
                return error(HttpStatus.SERVICE_UNAVAILABLE, "Spotify source is not enabled");
            }

            if ((trackId == null || trackId.isBlank()) && (isrc == null || isrc.isBlank())) {
                return error(HttpStatus.BAD_REQUEST, "Provide either 'trackId' or 'isrc' parameter");
            }

            String resolvedTrackId = trackId;

            if (resolvedTrackId == null || resolvedTrackId.isBlank()) {
                resolvedTrackId = spotify.resolveTrackIdFromIsrc(isrc);
                if (resolvedTrackId == null) {
                    return error(HttpStatus.NOT_FOUND, "No Spotify track found for ISRC: " + isrc);
                }
            }

            JsonNode lyrics = spotify.fetchColorLyrics(resolvedTrackId, artworkUrl);
            if (lyrics == null) {
                return error(HttpStatus.NOT_FOUND, "No lyrics found for track: " + resolvedTrackId);
            }

            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(mapper.writeValueAsString(lyrics));
        } catch (Exception e) {
            log.error("Error fetching color lyrics", e);
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch lyrics: " + e.getMessage());
        }
    }

    private ResponseEntity<String> error(HttpStatus status, String message) {
        ObjectNode json = mapper.createObjectNode();
        json.put("status", status.value());
        json.put("error", message);
        try {
            return ResponseEntity.status(status)
                    .header("Content-Type", "application/json")
                    .body(mapper.writeValueAsString(json));
        } catch (Exception e) {
            return ResponseEntity.status(status).body("{\"error\":\"" + message + "\"}");
        }
    }
}