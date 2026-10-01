package com.slugyzeon.plugin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import static com.slugyzeon.plugin.mirror.MirroringAudioSourceManager.ISRC_PATTERN;
import static com.slugyzeon.plugin.mirror.MirroringAudioSourceManager.QUERY_PATTERN;

@ConfigurationProperties(prefix = "plugins.slugyzeon")
@Component
public class SlugYZeonConfig {

    private String[] providers = {
            "dzisrc:" + ISRC_PATTERN,
            "ytsearch:\"" + ISRC_PATTERN + "\"",
            "ytsearch:" + QUERY_PATTERN
    };

    public String[] getProviders() {
        return providers;
    }

    public void setProviders(String[] providers) {
        this.providers = providers;
    }
}