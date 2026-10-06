<div align="center">

```
███████╗ ██████╗ ██╗      █████╗  ██████╗███████╗     █████╗ ██╗   ██╗██████╗ ██╗ ██████╗ 
██╔════╝██╔═══██╗██║     ██╔══██╗██╔════╝██╔════╝    ██╔══██╗██║   ██║██╔══██╗██║██╔═══██╗
███████╗██║   ██║██║     ███████║██║     █████╗      ███████║██║   ██║██║  ██║██║██║   ██║
╚════██║██║   ██║██║     ██╔══██║██║     ██╔══╝      ██╔══██║██║   ██║██║  ██║██║██║   ██║
███████║╚██████╔╝███████╗██║  ██║╚██████╗███████╗    ██║  ██║╚██████╔╝██████╔╝██║╚██████╔╝
╚══════╝ ╚═════╝ ╚══════╝╚═╝  ╚═╝ ╚═════╝╚══════╝    ╚═╝  ╚═╝ ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝ 
```

# 🎧 SolaceAudio
### *High-Throughput, Zero-Throttle Multi-Source Audio Engine for Lavalink v4*

<br/>

<p align="center">
  <img src="https://readme-typing-svg.demolab.com?font=JetBrains+Mono&weight=700&size=19&pause=1000&color=00F0FF&center=true&vCenter=true&random=false&width=620&lines=NEXT-GEN+AUDIO+ENGINE+FOR+LAVALINK+V4;HIGH-THROUGHPUT+IN-MEMORY+LRU+CACHE;MULTI-MARKET+SPOTIFY+FAILOVER+RING;AUTOMATED+DISK+QUOTA+AUTO-EVICTION;ZERO+RATE+LIMITS+%E2%80%A2+ZERO+CREDENTIALS" alt="SolaceAudio Typing SVG" />
</p>

<br/>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/Lavalink-4.0+-7289DA?style=for-the-badge&logo=discord&logoColor=white" />
  <img src="https://img.shields.io/badge/License-Apache_2.0-764ba2?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Cache-In--Memory%20LRU%20%3C1ms-39FF14?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Sources-6%20Active-00F0FF?style=for-the-badge" />
</p>

<br/>

<img src="https://capsule-render.vercel.app/api?type=waving&color=gradient&customColorList=0,2,26&height=100&section=header"/>

</div>

---

## ⚡ Why SolaceAudio?

Most standard Lavalink audio providers choke under heavy server load:
- **Disk Exhaustion**: Cache directories balloon to 50GB+, crashing the host VPS.
- **Search Latency**: Repeated search queries hit external APIs over and over, causing 3-5 second delays.
- **Geo-Block Drops**: Spotify tracks fail when queried from US servers if the track is licensed in other regions.

**SolaceAudio fixes all of this.** Engineered from the ground up for massive production Discord bots, SolaceAudio introduces hardened memory architectures, automatic cache quota lifecycles, and resilient multi-market failover rings.

---

## 🚀 Key Architectural Highlights

```
 ┌────────────────────────────────────────────────────────────────────────┐
 │                      SOLACEAUDIO STREAM PIPELINE                       │
 ├────────────────────────────────────────────────────────────────────────┤
 │  [ Client Request ]                                                    │
 │          │                                                             │
 │          ▼                                                             │
 │  [ In-Memory LRU Cache ] ──(Hit < 1ms)──> [ Instant AudioTrack Return] │
 │          │ (Miss)                                                      │
 │          ▼                                                             │
 │  [ Multi-Market Resolver ] ──> (US -> GB -> DE -> IN Fallback Ring)    │
 │          │                                                             │
 │          ▼                                                             │
 │  [ Managed Disk Stream ] ──> (LRU Enforced Eviction Quota: 10GB Max)   │
 └────────────────────────────────────────────────────────────────────────┘
```

- ⚡ **Ultra-Fast In-Memory LRU Cache**: Repeated searches resolve in **`< 1ms`** without touching external APIs.
- 🛡️ **Automated Disk Quota Management**: Configurable hard limit (e.g., `10GB`) with oldest-track auto-eviction. No more full-disk VPS crashes!
- 🔄 **Multi-Market Spotify Failover Ring**: Automatically cascades across regions (`US`, `GB`, `DE`, `IN`) when tracks are region-restricted.
- 🎯 **Spring Boot 3.2+ Compatible**: Full explicit reflection parameter mapping for `/v4/sessions/{sessionId}/players/{guildId}/recommendation`.
- 🎵 **Native LavaLyrics Integration**: Built-in synchronization with LavaLyrics for Spotify color lyrics.
- 📡 **Zero HTTP Dependencies**: Built 100% on Java's native high-performance `HttpClient`.

---

## 🌐 Supported Sources & Providers

| Source | Features Supported | Playback Mechanism |
| :--- | :--- | :---: |
| **Spotify** | Tracks, Albums, Playlists, Artists, Recommendations | High-Fidelity Mirroring |
| **Gaana** | Songs, Albums, Playlists, Artists | Native Akamai HLS Stream |
| **Amazon Music** | Tracks, Albums, Playlists, Artists | High-Fidelity Mirroring |
| **Pandora** | Tracks, Albums, Playlists, Artists, Stations | High-Fidelity Mirroring |
| **YouTube / Music** | Direct Tracks, Playlists, OEmbed, ATV Swapping | Direct Stream / Mirror |
| **Last.fm** | Scrobbler Recommendations & Similar Artists | Smart Audio Resolver |

---

## 📥 Installation

Download the latest compiled release **`solaceaudio-plugin.jar`** or build it directly from source:

```bash
./gradlew clean build -x test
```

Place the generated `solaceaudio-plugin.jar` into your Lavalink `plugins/` directory.

---

## ⚙️ Configuration (`application.yml`)

Add the **`solaceaudio`** configuration block to your Lavalink `application.yml`:

```yaml
plugins:
  solaceaudio:
    sources:
      spotify: true
      gaana: true
      amazonmusic: true
      pandora: true
      youtube: true
    spotify:
      market: "US"
      fallbackMarkets: ["GB", "DE", "IN"]
    cache:
      maxDiskCacheMb: 10240       # 10 GB disk hard cap
      maxSearchMemoryEntries: 5000 # In-memory LRU search cache
    lastfm:
      apiKey: "YOUR_LASTFM_KEY"  # Optional: For Last.fm recommendations
```

---

## 🧪 Diagnostics & Endpoints

| Endpoint | Method | Description |
| :--- | :---: | :--- |
| `/v4/sessions/{sessionId}/players/{guildId}/recommendation` | `GET` | Smart recommendations based on currently playing audio context |
| `/v4/solaceaudio/youtube/suggest` | `GET` | Real-time YouTube search autocomplete |

---

## 📄 License & Legal Notice

This project is licensed under the **Apache License 2.0**.

- Free to use, modify, distribute, and integrate commercially.
- **Notice & Derivative Work**: SolaceAudio is an enhanced and hardened distribution originally derived from [SlugYZeon](https://github.com/xylen-py/SlugYZeon) under the Apache License 2.0.

---

<div align="center">

<img src="https://capsule-render.vercel.app/api?type=waving&color=gradient&customColorList=0,2,26&height=80&section=footer"/>

<b>Crafted with ❤️ by <a href="https://github.com/titanxdevz">Nex Devz</a></b>

</div>