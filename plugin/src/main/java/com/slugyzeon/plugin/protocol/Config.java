package com.slugyzeon.plugin.protocol;

public class Config {
    private GaanaConfig gaana;
    private AmazonMusicConfig amazonmusic;
    private PandoraConfig pandora;
    private SpotifyConfig spotify;

    public GaanaConfig getGaana() { return gaana; }
    public void setGaana(GaanaConfig gaana) { this.gaana = gaana; }
    public AmazonMusicConfig getAmazonmusic() { return amazonmusic; }
    public void setAmazonmusic(AmazonMusicConfig amazonmusic) { this.amazonmusic = amazonmusic; }
    public PandoraConfig getPandora() { return pandora; }
    public void setPandora(PandoraConfig pandora) { this.pandora = pandora; }
    public SpotifyConfig getSpotify() { return spotify; }
    public void setSpotify(SpotifyConfig spotify) { this.spotify = spotify; }

    public static class GaanaConfig {
        private int playlistLoadLimit;
        private int albumLoadLimit;
        private int artistLoadLimit;
        public int getPlaylistLoadLimit() { return playlistLoadLimit; }
        public void setPlaylistLoadLimit(int playlistLoadLimit) { this.playlistLoadLimit = playlistLoadLimit; }
        public int getAlbumLoadLimit() { return albumLoadLimit; }
        public void setAlbumLoadLimit(int albumLoadLimit) { this.albumLoadLimit = albumLoadLimit; }
        public int getArtistLoadLimit() { return artistLoadLimit; }
        public void setArtistLoadLimit(int artistLoadLimit) { this.artistLoadLimit = artistLoadLimit; }
    }

    public static class AmazonMusicConfig {
        private int playlistLoadLimit;
        private int albumLoadLimit;
        private int artistLoadLimit;
        public int getPlaylistLoadLimit() { return playlistLoadLimit; }
        public void setPlaylistLoadLimit(int playlistLoadLimit) { this.playlistLoadLimit = playlistLoadLimit; }
        public int getAlbumLoadLimit() { return albumLoadLimit; }
        public void setAlbumLoadLimit(int albumLoadLimit) { this.albumLoadLimit = albumLoadLimit; }
        public int getArtistLoadLimit() { return artistLoadLimit; }
        public void setArtistLoadLimit(int artistLoadLimit) { this.artistLoadLimit = artistLoadLimit; }
    }

    public static class PandoraConfig {
        private int searchLimit;
        public int getSearchLimit() { return searchLimit; }
        public void setSearchLimit(int searchLimit) { this.searchLimit = searchLimit; }
    }

    public static class SpotifyConfig {
        private String spDc;
        private int playlistLoadLimit;
        private int albumLoadLimit;
        private Boolean resolveArtistsInSearch;
        private Boolean localFiles;
        
        public String getSpDc() { return spDc; }
        public void setSpDc(String spDc) { this.spDc = spDc; }
        public int getPlaylistLoadLimit() { return playlistLoadLimit; }
        public void setPlaylistLoadLimit(int playlistLoadLimit) { this.playlistLoadLimit = playlistLoadLimit; }
        public int getAlbumLoadLimit() { return albumLoadLimit; }
        public void setAlbumLoadLimit(int albumLoadLimit) { this.albumLoadLimit = albumLoadLimit; }
        public Boolean getResolveArtistsInSearch() { return resolveArtistsInSearch; }
        public void setResolveArtistsInSearch(Boolean resolveArtistsInSearch) { this.resolveArtistsInSearch = resolveArtistsInSearch; }
        public Boolean getLocalFiles() { return localFiles; }
        public void setLocalFiles(Boolean localFiles) { this.localFiles = localFiles; }
    }
}
