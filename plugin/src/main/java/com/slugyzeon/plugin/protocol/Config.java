package com.slugyzeon.plugin.protocol;

public class Config {

    private GaanaConfig gaana;
    private AmazonMusicConfig amazonmusic;
    private SpotifyConfig spotify;
    private PandoraConfig pandora;

    public GaanaConfig getGaana() {
        return this.gaana;
    }

    public void setGaana(GaanaConfig gaana) {
        this.gaana = gaana;
    }

    public AmazonMusicConfig getAmazonmusic() {
        return this.amazonmusic;
    }

    public void setAmazonmusic(AmazonMusicConfig amazonmusic) {
        this.amazonmusic = amazonmusic;
    }

    public SpotifyConfig getSpotify() {
        return this.spotify;
    }

    public void setSpotify(SpotifyConfig spotify) {
        this.spotify = spotify;
    }

    public PandoraConfig getPandora() {
        return this.pandora;
    }

    public void setPandora(PandoraConfig pandora) {
        this.pandora = pandora;
    }

    public static class GaanaConfig {
        private int playlistLoadLimit;
        private int albumLoadLimit;
        private int artistLoadLimit;

        public int getPlaylistLoadLimit() {
            return this.playlistLoadLimit;
        }

        public void setPlaylistLoadLimit(int playlistLoadLimit) {
            this.playlistLoadLimit = playlistLoadLimit;
        }

        public int getAlbumLoadLimit() {
            return this.albumLoadLimit;
        }

        public void setAlbumLoadLimit(int albumLoadLimit) {
            this.albumLoadLimit = albumLoadLimit;
        }

        public int getArtistLoadLimit() {
            return this.artistLoadLimit;
        }

        public void setArtistLoadLimit(int artistLoadLimit) {
            this.artistLoadLimit = artistLoadLimit;
        }
    }

    public static class AmazonMusicConfig {
        private int playlistLoadLimit;
        private int albumLoadLimit;
        private int artistLoadLimit;

        public int getPlaylistLoadLimit() {
            return this.playlistLoadLimit;
        }

        public void setPlaylistLoadLimit(int playlistLoadLimit) {
            this.playlistLoadLimit = playlistLoadLimit;
        }

        public int getAlbumLoadLimit() {
            return this.albumLoadLimit;
        }

        public void setAlbumLoadLimit(int albumLoadLimit) {
            this.albumLoadLimit = albumLoadLimit;
        }

        public int getArtistLoadLimit() {
            return this.artistLoadLimit;
        }

        public void setArtistLoadLimit(int artistLoadLimit) {
            this.artistLoadLimit = artistLoadLimit;
        }
    }

    public static class SpotifyConfig {
        private String spDc;
        private int playlistLoadLimit;
        private int albumLoadLimit;
        private Boolean resolveArtistsInSearch;
        private Boolean localFiles;

        public String getSpDc() {
            return this.spDc;
        }

        public void setSpDc(String spDc) {
            this.spDc = spDc;
        }

        public int getPlaylistLoadLimit() {
            return this.playlistLoadLimit;
        }

        public void setPlaylistLoadLimit(int playlistLoadLimit) {
            this.playlistLoadLimit = playlistLoadLimit;
        }

        public int getAlbumLoadLimit() {
            return this.albumLoadLimit;
        }

        public void setAlbumLoadLimit(int albumLoadLimit) {
            this.albumLoadLimit = albumLoadLimit;
        }

        public Boolean getResolveArtistsInSearch() {
            return this.resolveArtistsInSearch;
        }

        public void setResolveArtistsInSearch(Boolean resolveArtistsInSearch) {
            this.resolveArtistsInSearch = resolveArtistsInSearch;
        }

        public Boolean getLocalFiles() {
            return this.localFiles;
        }

        public void setLocalFiles(Boolean localFiles) {
            this.localFiles = localFiles;
        }
    }

    public static class PandoraConfig {
        private int searchLimit;

        public int getSearchLimit() {
            return this.searchLimit;
        }

        public void setSearchLimit(int searchLimit) {
            this.searchLimit = searchLimit;
        }
    }
}
