package com.axiel7.anihyou.core.streaming.api

import android.util.Base64
import com.axiel7.anihyou.core.streaming.model.Episode
import com.axiel7.anihyou.core.streaming.model.EpisodeList
import com.axiel7.anihyou.core.streaming.model.PlaybackInfo
import com.axiel7.anihyou.core.streaming.model.SubtitleTrack
import com.axiel7.anihyou.core.streaming.model.VideoSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * AllAnime streaming provider.
 *
 * AllAnime exposes an unauthenticated GraphQL API at api.allanime.day.
 * This is the same API used by the AllAnime website and several open-source
 * anime apps. No API key required.
 *
 * Flow:
 *  1. Query `searchAnime` with the AniList ID (allAnime stores it as `malId` mapping,
 *     but also supports AniList via the `idMal` cross-reference or title search).
 *  2. Query `show` to get episode list with thumbnails/titles.
 *  3. Query `episodeSources` to get stream servers for a specific episode.
 *  4. Resolve the chosen server URL to a direct HLS/mp4 link.
 */
class AllAnimeProvider(
    private val okHttpClient: OkHttpClient,
) : StreamingProvider {

    override val name = "AllAnime"

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val apiUrl = "https://api.allanime.day/api"
    private val referer = "https://allanime.to"
    private val userAgent = "Mozilla/5.0 (Android 14; Mobile) AppleWebKit/537.36"

    // ── Search ──────────────────────────────────────────────────────────

    override suspend fun findAnimeId(anilistId: Int, title: String): String? =
        withContext(Dispatchers.IO) {
            runCatching {
                val query = """
                    query(${'$'}search: SearchInput, ${'$'}limit: Int) {
                        shows(search: ${'$'}search, limit: ${'$'}limit) {
                            edges { _id name }
                        }
                    }
                """.trimIndent()
                val variables = """{"search":{"query":"${title.replace("\"", "")}", "allowAdult": false},"limit":5}"""
                val response = graphql(query, variables) ?: return@runCatching null
                val result = json.decodeFromString<SearchResponse>(response)
                // Prefer exact title match
                result.data?.shows?.edges
                    ?.firstOrNull { it.name?.contains(title, ignoreCase = true) == true }
                    ?._id
                    ?: result.data?.shows?.edges?.firstOrNull()?._id
            }.getOrNull()
        }

    // ── Episodes ─────────────────────────────────────────────────────────

    override suspend fun getEpisodes(animeId: String, isDub: Boolean): EpisodeList =
        withContext(Dispatchers.IO) {
            runCatching {
                val dubSub = if (isDub) "dub" else "sub"
                val query = """
                    query(${'$'}showId: String!, ${'$'}episodeNumStart: Float, ${'$'}episodeNumEnd: Float) {
                        show(_id: ${'$'}showId) {
                            _id
                            name
                            availableEpisodesDetail
                        }
                        episodeInfos(showId: ${'$'}showId, episodeNumStart: ${'$'}episodeNumStart, episodeNumEnd: ${'$'}episodeNumEnd) {
                            episodeIdNum
                            notes
                            thumbnails
                            uploadDates
                        }
                    }
                """.trimIndent()
                val variables = """{"showId":"$animeId","episodeNumStart":1,"episodeNumEnd":9999}"""
                val response = graphql(query, variables) ?: return@runCatching EpisodeList(animeId, emptyList())
                val result = json.decodeFromString<EpisodesResponse>(response)

                val infos = result.data?.episodeInfos ?: emptyList()
                val episodes = infos.map { info ->
                    Episode(
                        number = info.episodeIdNum ?: 0f,
                        title = info.notes,
                        description = null,
                        thumbnail = info.thumbnails?.firstOrNull(),
                        isDub = isDub,
                        sourceEpisodeId = "$animeId|${info.episodeIdNum}|$dubSub",
                    )
                }.sortedBy { it.number }

                EpisodeList(animeId, episodes)
            }.getOrElse { EpisodeList(animeId, emptyList()) }
        }

    // ── Streams ──────────────────────────────────────────────────────────

    override suspend fun getPlaybackInfo(episodeId: String): PlaybackInfo =
        withContext(Dispatchers.IO) {
            runCatching {
                val parts = episodeId.split("|")
                val showId = parts[0]
                val epNum = parts[1]
                val dubSub = parts.getOrElse(2) { "sub" }

                val query = """
                    query(${'$'}showId: String!, ${'$'}translationType: VaildTranslationTypeEnumType!, ${'$'}episodeString: String!) {
                        episode(showId: ${'$'}showId, translationType: ${'$'}translationType, episodeString: ${'$'}episodeString) {
                            sourceUrls
                        }
                    }
                """.trimIndent()
                val variables = """{"showId":"$showId","translationType":"$dubSub","episodeString":"$epNum"}"""
                val response = graphql(query, variables) ?: return@runCatching PlaybackInfo(emptyList())
                val result = json.decodeFromString<EpisodeSourceResponse>(response)

                val sourceUrls = result.data?.episode?.sourceUrls ?: emptyList()

                // Resolve each source URL — filter to working HLS streams
                val sources = sourceUrls.mapNotNull { src ->
                    resolveSource(src)
                }.flatten()

                PlaybackInfo(sources = sources)
            }.getOrElse { PlaybackInfo(emptyList()) }
        }

    // ── Internal helpers ──────────────────────────────���───────────────────────

    private fun graphql(query: String, variables: String): String? {
        return runCatching {
            val body = """{"query":"${query.replace("\n", " ").replace("\"", "\\\"")}","variables":$variables}"""
                .toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(apiUrl)
                .post(body)
                .addHeader("Referer", referer)
                .addHeader("User-Agent", userAgent)
                .build()
            okHttpClient.newCall(request).execute().body?.string()
        }.getOrNull()
    }

    private fun resolveSource(rawUrl: String): List<VideoSource>? {
        // AllAnime returns base64-encoded URLs prefixed with "--"
        val url = if (rawUrl.startsWith("--")) {
            try {
                String(Base64.decode(rawUrl.substring(2), Base64.DEFAULT))
            } catch (_: Exception) {
                rawUrl
            }
        } else rawUrl

        if (url.isBlank()) return null

        return when {
            url.contains(".m3u8") -> listOf(
                VideoSource(url = url, quality = "auto", isM3U8 = true)
            )
            url.contains("mp4") -> listOf(
                VideoSource(url = url, quality = "720p", isM3U8 = false)
            )
            else -> null
        }
    }

    // ── Response models ───────────────────────────────────────────────────────

    @Serializable
    data class SearchResponse(val data: SearchData? = null)
    @Serializable
    data class SearchData(val shows: ShowsEdges? = null)
    @Serializable
    data class ShowsEdges(val edges: List<ShowEdge>? = null)
    @Serializable
    data class ShowEdge(val _id: String? = null, val name: String? = null)

    @Serializable
    data class EpisodesResponse(val data: EpisodesData? = null)
    @Serializable
    data class EpisodesData(
        val show: ShowDetail? = null,
        val episodeInfos: List<EpisodeInfo>? = null,
    )
    @Serializable
    data class ShowDetail(val _id: String? = null, val name: String? = null)
    @Serializable
    data class EpisodeInfo(
        val episodeIdNum: Float? = null,
        val notes: String? = null,
        val thumbnails: List<String>? = null,
    )

    @Serializable
    data class EpisodeSourceResponse(val data: EpisodeSourceData? = null)
    @Serializable
    data class EpisodeSourceData(val episode: EpisodeSource? = null)
    @Serializable
    data class EpisodeSource(val sourceUrls: List<String>? = null)
}
