[![](https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://java.com)
[![](https://img.shields.io/badge/Lavalink-4.0+-7289DA?style=for-the-badge)](https://github.com/lavalink-devs/Lavalink)
[![](https://img.shields.io/badge/License-Apache_2.0-764ba2?style=for-the-badge)](LICENSE)
[![](https://img.shields.io/badge/Sources-8-667eea?style=for-the-badge)](#sources)
[![](https://img.shields.io/badge/HTTP_Deps-Zero-00C853?style=for-the-badge)](#features)

# SlugYZeon

> [!NOTE]
> Multi-source lavalink plugin featuring 5 audio sources, zero rate limits, and zero credentials needed. Built entirely with Java's native `HttpClient`.

## Summary

* [Sources](#sources)
    * [Features](#features)
    * [What is Mirroring?](#what-is-mirroring)
* [Lavalink Usage](#lavalink-usage)
    * [Configuration](#configuration)
* [Supported URLs and Queries](#supported-urls-and-queries)
* [Credits](#credits)

# Sources

| Source         | Features                                         | Playback                     |
|----------------|--------------------------------------------------|------------------------------|
| Amazon Music   | tracks, albums, playlists, artists               | [Mirror](#what-is-mirroring) |
| Spotify        | tracks, albums, playlists, artists               | [Mirror](#what-is-mirroring) |
| Gaana          | songs, albums, playlists, artists                | Native Stream (HLS)          |
| Pandora        | tracks, albums, playlists, artists, stations     | [Mirror](#what-is-mirroring) |
| YouTube        | tracks, searches, oEmbed, streams                | Direct / [Mirror](#what-is-mirroring) |

### Features

- **Mirror System** — ISRC-first resolution with automatic query fallback for mirrored sources.
- **YouTube Client Rotation** — Rotates between WEB, ANDROID, IOS, TVHTML5, and WEB_EMBEDDED clients with cooldown tracking.
- **PoToken Session Warmer & Pool** — Autonomous visitor session pool with background renewal every 30 minutes.
- **Adaptive Range Streaming** — Throttling mitigation using HTTP Range requests and multi-format audio fallback candidate stepping.
- **ATV Counterpart Track Swapping** — Detects music videos and swaps in clean YouTube Music audio tracks (`MUSIC_VIDEO_TYPE_ATV`) to bypass video skits and intro chatter.
- **Dual-Format Disk Cache & Sidecars** — Verifies cached WebM/M4A audio headers and maintains structured `<videoId>.json` metadata sidecars.
- **Search Autocomplete Endpoint** — Provides real-time search query suggestions via `/v4/slugyzeon/youtube/suggest`.
- **Bot & PoToken Protection** — Soft-fails blocked clients on 429s, 403s, and login/bot challenges, rotating to next available client.
- **Region & Availability Bypass** — Retries unavailable or geo-blocked tracks with alternate region parameters before failing.
- **Automatic Mirror Fallback** — Automatically routes failed YouTube tracks to mirror providers without surfacing errors.
- **Spotify Resilient Scraper** — Embedded zero-day nuance table, embed session token fallback, HTTP/2 pipelined ISRC resolution, and dynamic GraphQL hash hot-patching.
- **Spotify Canvas Extraction** — Resolves animated canvas MP4 video URLs for tracks via `spclient.wg.spotify.com/canvaz-cache`.
- **Multi-Seed Recommendations** — Supports `sprec:` queries with multiple track and artist seeds (`seed_tracks=`, `seed_artists=`).
- **Gaana Native Streaming** — Fully persistent HLS chunk buffering directly from Akamai CDN.
- **Rich Metadata** — Returns extended playlists, ISRC codes, album/artist URLs, and preview URLs.
- **Native Lyrics** — Built-in integration with LavaLyrics for Spotify color lyrics.
- **Zero HTTP Dependencies** — Relies entirely on Java's native `HttpClient` for maximal performance.
- **Seamless Integration** — Plugs directly into standard Lavalink 4.0+ via spring boot.

> [!IMPORTANT]
> ### What is Mirroring?
>
> Mirroring is the process of taking the metadata resolved from one source and using it to retrieve a playable `AudioTrack` from another.
>
> For example, SlugYZeon cannot directly play from Spotify, or any source marked as `Mirror` playback, so it automatically falls back to searching YouTube for the track's ISRC or Title.

## Lavalink Usage

This plugin requires Lavalink `v4` or greater.

To install this plugin, add the following into your `application.yml`:

```yaml
lavalink:
    plugins:
        - dependency: "com.github.xylen-py.SlugYZeon:slugyzeon-plugin:VERSION"
          repository: https://jitpack.io
          snapshot: false
```

### Configuration

> [!WARNING]
> The `plugins` object MUST be at the root of your YAML configuration file.

```yaml
plugins:
  slugyzeon:
    providers: # Custom providers for track loading. This is the default
      # - "dzisrc:%ISRC%" # Deezer ISRC provider
      # - "dzsearch:%QUERY%" # Deezer search provider
      - "ytsearch:\"%ISRC%\"" # Will be ignored if track does not have an ISRC. See https://en.wikipedia.org/wiki/International_Standard_Recording_Code
      - "ytsearch:%QUERY%" # Will be used if track has no ISRC or no track could be found for the ISRC
      #  you can add multiple other fallback sources here
    sources:
      gaana: false # Enable Gaana source
      amazonmusic: false # Enable Amazon Music source
      spotify: false # Enable Spotify source
      pandora: false # Enable Pandora source
      youtube: false # Enable YouTube-SlugYZeon source (requires the new Youtube Source plugin)
    youtube:
      oembed: false # Use youtube.com/oembed?url= to resolve tracks
      mirror: false # Use active Lavalink sources to mirror tracks if direct YouTube playback fails
      mirrorProviders: # Custom fallback providers when direct YouTube playback fails
        # - "spsearch:%QUERY%"
        # - "dzsearch:%QUERY%"
        - "scsearch:%QUERY%"
      localDiskCache: false # Enable local disk caching for streamed audio
      diskCachePath: "youtube-cache" # Directory path for local audio cache
      cipherUrl: "https://cipher.kikkia.dev" # External cipher decryption service endpoint
    spotify:
      spDc: "your spDc cookie" # Required to fetch lyrics (works with free or premium accounts)
      countryCode: "US" # the country code for filtering artist top tracks
      playlistLoadLimit: 6 # The number of pages at 100 tracks each
      albumLoadLimit: 6 # The number of pages at 50 tracks each
      resolveArtistsInSearch: true # Whether to resolve artists in track search results
      localFiles: false # Enable local files support
    gaana:
      apiUrl: "https://gaana-plugin-api.vercel.app/api" # The API proxy required to resolve Gaana HLS manifests
      playlistLoadLimit: 50
      albumLoadLimit: 50
      artistLoadLimit: 50
      searchLimit: 25
    amazonmusic:
      apiUrl: "https://amazon-plugin-api.vercel.app/api" # The API proxy required to resolve Amazon Music endpoints
      playlistLoadLimit: 50
      albumLoadLimit: 50
      artistLoadLimit: 50
    pandora:
      csrfToken: "your csrftoken" # Manual CSRF cookie from pandora.com (Only works if node is hosted inside the US)
      searchLimit: 6
```

### Live Configuration Updates

You can dynamically update your configuration at runtime without restarting Lavalink! Simply send a `PATCH` request to the `/v4/slugyzeon/config` endpoint with the new configuration JSON and your Lavalink password in the `Authorization` header.

**Example Payload:**
```json
{
  "spotify": {
    "spDc": "your spDc cookie",
    "playlistLoadLimit": 6,
    "albumLoadLimit": 6,
    "resolveArtistsInSearch": true,
    "localFiles": false
  },
  "gaana": {
    "playlistLoadLimit": 50,
    "albumLoadLimit": 50,
    "artistLoadLimit": 50
  },
  "amazonmusic": {
    "playlistLoadLimit": 50,
    "albumLoadLimit": 50,
    "artistLoadLimit": 50
  },
  "pandora": {
    "searchLimit": 6
  },
  "youtube": {
    "oembed": false,
    "mirror": true,
    "mirrorProviders": ["scsearch:%QUERY%"],
    "localDiskCache": true,
    "diskCachePath": "youtube-cache",
    "cipherUrl": "https://cipher.kikkia.dev"
  }
}
```

---

## Supported URLs and Queries

### Spotify

```bash
# search
GET /v4/loadtracks?identifier=spsearch:Shape of You

# recommendations
GET /v4/loadtracks?identifier=sprec:seed_tracks=trackId&limit=10

# url support
GET /v4/loadtracks?identifier=https://open.spotify.com/track/7qiZfU4dY1lWllzX7mPBI3
GET /v4/loadtracks?identifier=https://open.spotify.com/album/1ATL5GLyefJaxhQzSPVrLX
GET /v4/loadtracks?identifier=https://open.spotify.com/playlist/37i9dQZF1DXcBWIGoYBM5M
GET /v4/loadtracks?identifier=https://open.spotify.com/artist/1Xyo4u8uXC1ZmMpatF05PJ
```

### Gaana

```bash
# search
GET /v4/loadtracks?identifier=gnsearch:Tum Hi Ho

# recommendations
GET /v4/loadtracks?identifier=gnrec:bollywood

# url support
GET /v4/loadtracks?identifier=https://gaana.com/song/tum-hi-ho
GET /v4/loadtracks?identifier=https://gaana.com/album/aashiqui-2
GET /v4/loadtracks?identifier=https://gaana.com/playlist/gaana-dj-hindi-top-50-1
GET /v4/loadtracks?identifier=https://gaana.com/artist/arijit-singh
```

### Amazon Music

```bash
# search
GET /v4/loadtracks?identifier=azsearch:Shape of You

# url support
GET /v4/loadtracks?identifier=https://music.amazon.com/tracks/B07QGZ1GJ6
GET /v4/loadtracks?identifier=https://music.amazon.com/albums/B07QGZX5BX
GET /v4/loadtracks?identifier=https://music.amazon.com/playlists/B07QGZ1GJ6
GET /v4/loadtracks?identifier=https://music.amazon.com/artists/B001GBY2LE
```

### Pandora

```bash
# search
GET /v4/loadtracks?identifier=pdsearch:Bohemian Rhapsody

# recommendations
GET /v4/loadtracks?identifier=pdrec:TRxxxxxx

# url support
GET /v4/loadtracks?identifier=https://www.pandora.com/artist/queen/bohemian-rhapsody/TRxxxxxx
GET /v4/loadtracks?identifier=https://www.pandora.com/artist/queen/a-night-at-the-opera/ALxxxxxx
GET /v4/loadtracks?identifier=https://www.pandora.com/playlist/PLxxxxxx
GET /v4/loadtracks?identifier=https://www.pandora.com/station/STxxxxxx
```

### YouTube

```bash
# search
GET /v4/loadtracks?identifier=ytsearch:Never Gonna Give You Up
GET /v4/loadtracks?identifier=ytmsearch:Never Gonna Give You Up

# search suggestions / autocomplete
GET /v4/slugyzeon/youtube/suggest?query=Never+Gonna

# url support
GET /v4/loadtracks?identifier=https://www.youtube.com/watch?v=dQw4w9WgXcQ
GET /v4/loadtracks?identifier=https://youtu.be/dQw4w9WgXcQ
GET /v4/loadtracks?identifier=https://www.youtube.com/shorts/dQw4w9WgXcQ
```

---

## Build

```bash
./gradlew clean build
```

> Built plugin jar is output to `plugin/build/libs/`

---

## Credits

- **[xylen-py](https://github.com/xylen-py)** — For plugin APIs & sources for Gaana & Amazon Music.
- **[saraansx](https://github.com/saraansx)** — For help with Spotify integration.
- **[lavalink-devs](https://github.com/lavalink-devs/lavalink-plugin-template)** — For providing the official Lavalink plugin template.
- **[topi314 / LavaSrc](https://github.com/topi314/LavaSrc)** — For the foundational mirroring architecture and code structure.

---

## Disclaimer

This plugin is provided for **educational and research purposes only**. It is a learning project to understand audio streaming, API development, and Lavalink plugin architecture. Use responsibly and respect each platform's terms of service. The authors are not responsible for any misuse.

---

## License

Licensed under the **Apache License 2.0**.

- You **can** use, modify, and distribute this software.
- You **can** use it in commercial projects.
- You **must** include the license notice, state changes, and provide original copyright.

See [LICENSE](LICENSE) for full details.

---

<div align="center">

<br>

<b>built by <a href="https://github.com/xylen-py">xylen</a> — draxity engine</b>

<br>

`.1xylen SlugYZeon v4.0.0 - Lavalink`

<br><br>

</div>