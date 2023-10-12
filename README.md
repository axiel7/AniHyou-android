# <img alt="app-icon" height="50" src="https://github.com/axiel7/AniHyou-android/blob/master/app/src/main/res/mipmap-hdpi/ic_launcher_round.webp"/>AniHyou

[![Donate](https://img.shields.io/badge/buy%20me%20a%20coffee-donate-yellow.svg)](https://ko-fi.com/axiel7)

Another unofficial Android AniList client

[<img alt="Google Play" height="80" src="https://play.google.com/intl/en_US/badges/images/generic/en_badge_web_generic.png"/>](https://play.google.com/store/apps/details?id=com.axiel7.anihyou)

iOS version [here](https://github.com/axiel7/AniHyou)

Get early beta versions and follow the development on the official Discord server:

[![Discord Banner 3](https://discordapp.com/api/guilds/741059285122940928/widget.png?style=banner2)](https://discord.gg/CTv3WdfxHh)

# Screenshots
![Screenshots](https://github.com/axiel7/AniHyou-android/blob/master/screenshots.webp)

## Coming features
- [See project](https://github.com/users/axiel7/projects/2/views/1)

# Donate 💸
Support the development of AniHyou by making a donation via:

[Ko-Fi](https://ko-fi.com/axiel7)

BTC
```
3KKjJuorh9se2jUo1Hr6MFgXhnBWbj5fTP
```

ETH
```
0xBd20dD0e036B246F879EeFde52601f0fBbeC69c0
```

LTC
```
MRw5XPLsM9SVf48tv4nwQoY12nMXaiVzmD
```

## Libraries used
* [AniList GraphQL API](https://github.com/AniList/ApiV2-GraphQL-Docs)
* [Apollo Kotlin](https://github.com/apollographql/apollo-kotlin)
* [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
* [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
* Forked [compose-markdown](https://github.com/axiel7/compose-markdown)
* [MaterialKolor](https://github.com/jordond/MaterialKolor)
* [Hilt](https://dagger.dev/hilt)
* [Coil](https://github.com/coil-kt/coil)
* [Material3 Components](https://github.com/material-components/material-components-android)
* [Jetpack Compose](https://developer.android.com/jetpack/compose)
* [Jetpack Glance](https://developer.android.com/jetpack/compose/glance)

# Building
Put the following content on your local.properties file:

```properties
CLIENT_ID=1234 # your AniList API client ID here
```

To get a Client ID, go to the [Developer section](https://anilist.co/settings/developer) and create a new client with the *Redirect URL* set to `anihyou://auth-response`
