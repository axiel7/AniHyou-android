# <img alt="app-icon" height="50" src="https://github.com/axiel7/AniHyou-android/blob/master/core/resources/src/main/res/mipmap-hdpi/ic_launcher_round.webp"/>AniHyou — Shatter Fork

[![Downloads](https://img.shields.io/github/downloads/Xhanti-mbasa/AniHyou-android/total.svg)](https://github.com/Xhanti-mbasa/AniHyou-android/releases/latest)

A personal fork of [axiel7/AniHyou-android](https://github.com/axiel7/AniHyou-android) with custom features built on top of the original unofficial Android AniList client.

> Download the latest debug build from [Actions](https://github.com/Xhanti-mbasa/AniHyou-android/actions) → most recent **Build APK** run → **app-foss-debug** artifact.

---

## Changes in this fork

### 🎨 Custom app icon
The default AniHyou icon has been replaced with the Shatter logo.
- **Light mode** — black mark on white background
- **Dark mode** — white mark on black background

Android picks the correct variant automatically based on system theme via `values-night` color resources.

### ▶️ Miruro streaming integration
Every anime's info tab now includes a **Miruro** chip in the Streaming Sites section. Tapping it opens [miruro.to](https://miruro.to) with the anime pre-searched by title, taking you directly to available streams.

### 📺 Currently Watching on Home
The home Discover screen now has a **Continue Watching** row (Netflix-style) showing your in-progress anime from your AniList list, with episode progress displayed under each card.

### 📅 Calendar — Month / Week / Day views
The airing calendar has been extended with three view modes selectable via a segmented button at the top:
- **Month** — grid overview of the whole month
- **Week** — horizontal scroll through each day of the week (default)
- **Day** — single-day focus with time-ordered airing slots

### 🔑 TVDB API key in Settings
A **TVDB API Key** field has been added under Settings. Paste your key from [thetvdb.com/api-information](https://thetvdb.com/api-information) to enable the dub schedule tab on anime detail pages. The key is stored locally on-device.

---

## Building

The repo uses GitHub Actions to build automatically on every push to `master`.

To build manually:
```bash
./gradlew assembleFossDebug
```

---

## Upstream

This fork tracks [axiel7/AniHyou-android](https://github.com/axiel7/AniHyou-android). For issues with core AniList functionality, refer to the upstream repo.

iOS version: [axiel7/AniHyou-iOS](https://github.com/axiel7/AniHyou-iOS)
