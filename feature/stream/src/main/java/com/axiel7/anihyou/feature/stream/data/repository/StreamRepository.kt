package com.axiel7.anihyou.feature.stream.data.repository

import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.feature.stream.data.model.AnimeInfoResponse
import com.axiel7.anihyou.feature.stream.data.model.Episode
import com.axiel7.anihyou.feature.stream.data.model.EpisodeListResponse
import com.axiel7.anihyou.feature.stream.data.model.PagedAnimeResponse
import com.axiel7.anihyou.feature.stream.data.model.SpotlightResponse
import com.axiel7.anihyou.feature.stream.data.model.StreamSourcesResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

private const val MIRURO_BASE = "https://miruro-api.vercel.app"

/**
 * Central repository for all stream-tab data.
 *
 * Metadata calls (search, trending, info, etc.) hit the Miruro REST API which wraps AniList.
 * Streaming calls (episodes, sources) hit the Miruro secure/pipe tunnel via [MiruroPipeClient].
 *
 * The Miruro API base URL is configurable — users can self-host the Python API and point
 * to their own instance via Settings → Stream API URL.
 */
class StreamRepository(
    okHttpClient: OkHttpClient,
    private val baseUrlProvider: suspend () -> String = { MIRURO_BASE },
) {
    private val pipe = MiruroPipeClient(okHttpClient)
    private val json = pipe.json
    private val restClient = okHttpClient

    // ── REST helpers ──────────────────────────────────────────────────────────

    private suspend fun get(path: String): String = withContext(Dispatchers.IO) {
        val base = baseUrlProvider().trimEnd('/')
        val request = Request.Builder()
            .url("$base$path")
            .addHeader("Accept", "application/json")
            .build()
        restClient.newCall(request).execute().body?.string()
            ?: error("Empty response for $path")
    }

    private inline fun <reified T> String.decode(): T = json.decodeFromString(this)

    // ── Browse / discovery ────────────────────────────────────────────────────

    suspend fun getSpotlight(): DataResult<SpotlightResponse> = runCatching {
        DataResult.Success(get("/spotlight").decode<SpotlightResponse>())
    }.getOrElse { DataResult.Error(it.message ?: "spotlight failed") }

    suspend fun getTrending(page: Int = 1, perPage: Int = 20): DataResult<PagedAnimeResponse> =
        runCatching {
            DataResult.Success(
                get("/trending?page=$page&per_page=$perPage").decode<PagedAnimeResponse>()
            )
        }.getOrElse { DataResult.Error(it.message ?: "trending failed") }

    suspend fun getPopular(page: Int = 1, perPage: Int = 20): DataResult<PagedAnimeResponse> =
        runCatching {
            DataResult.Success(
                get("/popular?page=$page&per_page=$perPage").decode<PagedAnimeResponse>()
            )
        }.getOrElse { DataResult.Error(it.message ?: "popular failed") }

    suspend fun getRecent(page: Int = 1, perPage: Int = 20): DataResult<PagedAnimeResponse> =
        runCatching {
            DataResult.Success(
                get("/recent?page=$page&per_page=$perPage").decode<PagedAnimeResponse>()
            )
        }.getOrElse { DataResult.Error(it.message ?: "recent failed") }

    suspend fun getUpcoming(page: Int = 1, perPage: Int = 20): DataResult<PagedAnimeResponse> =
        runCatching {
            DataResult.Success(
                get("/upcoming?page=$page&per_page=$perPage").decode<PagedAnimeResponse>()
            )
        }.getOrElse { DataResult.Error(it.message ?: "upcoming failed") }

    suspend fun search(
        query: String,
        page: Int = 1,
        perPage: Int = 20,
    ): DataResult<PagedAnimeResponse> = runCatching {
        val encoded = java.net.URLEncoder.encode(query, "UTF-8")
        DataResult.Success(
            get("/search?query=$encoded&page=$page&per_page=$perPage").decode<PagedAnimeResponse>()
        )
    }.getOrElse { DataResult.Error(it.message ?: "search failed") }

    // ── Anime info ────────────────────────────────────────────────────────────

    suspend fun getAnimeInfo(anilistId: Int): DataResult<AnimeInfoResponse> = runCatching {
        DataResult.Success(get("/info/$anilistId").decode<AnimeInfoResponse>())
    }.getOrElse { DataResult.Error(it.message ?: "info failed") }

    // ── Streaming — via Miruro pipe ───────────────────────────────────────────

    /**
     * Step 1: Get all episode lists for an anime across all providers.
     * Returns the raw [EpisodeListResponse] with provider → sub/dub episode maps.
     */
    suspend fun getEpisodes(anilistId: Int): DataResult<EpisodeListResponse> = runCatching {
        val rawJson = pipe.pipeGet("episodes", mapOf("anilistId" to anilistId.toString()))
        val data = json.decodeFromString<EpisodeListResponse>(rawJson)
        // Inject slugified IDs matching the /watch/{provider}/{id}/{cat}/{slug} pattern
        DataResult.Success(injectSlugs(data, anilistId))
    }.getOrElse { DataResult.Error(it.message ?: "episodes failed") }

    /**
     * Step 2: Get streaming sources for a specific episode slug.
     * The [episodeSlug] is the `id` field from an [Episode] object — e.g.
     * "watch/kiwi/178005/sub/animepahe-1"
     *
     * Internally this re-fetches episodes to resolve the slug back to the original
     * encoded ID, then calls the /sources pipe endpoint.
     */
    suspend fun getSources(
        provider: String,
        anilistId: Int,
        category: String,
        slug: String,
    ): DataResult<StreamSourcesResponse> = runCatching {
        // Resolve slug → original episode ID
        val epJson = pipe.pipeGet("episodes", mapOf("anilistId" to anilistId.toString()))
        val epData = json.decodeFromString<EpisodeListResponse>(epJson)

        val providerData = epData.providers[provider]
            ?: error("Provider '$provider' not found for $anilistId")
        val epList = if (category == "dub") providerData.episodes.dub else providerData.episodes.sub

        val targetId = epList.firstOrNull { ep ->
            val prefix = if (':' in ep.id) ep.id.substringBefore(':') else ep.id
            "$prefix-${ep.number}" == slug
        }?.id ?: error("Slug '$slug' not found in provider $provider/$category")

        // Encode the original ID and call /sources
        val encodedId = pipe.encodeEpisodeId(targetId)
        val sourcesJson = pipe.pipeGet(
            "sources",
            mapOf(
                "episodeId" to encodedId,
                "provider" to provider,
                "category" to category,
                "anilistId" to anilistId.toString(),
            )
        )
        DataResult.Success(json.decodeFromString<StreamSourcesResponse>(sourcesJson))
    }.getOrElse { DataResult.Error(it.message ?: "sources failed") }

    // ── Internal helpers ──────────────────────────────────────────────────────

    /**
     * Mirrors the Python `_inject_source_slugs` function:
     * transforms raw episode IDs into "watch/{provider}/{anilistId}/{cat}/{prefix}-{number}"
     */
    private fun injectSlugs(data: EpisodeListResponse, anilistId: Int): EpisodeListResponse {
        val newProviders = data.providers.mapValues { (providerName, providerData) ->
            val newSub = providerData.episodes.sub.map { ep ->
                val prefix = if (':' in ep.id) ep.id.substringBefore(':') else ep.id
                ep.copy(id = "watch/$providerName/$anilistId/sub/$prefix-${ep.number}")
            }
            val newDub = providerData.episodes.dub.map { ep ->
                val prefix = if (':' in ep.id) ep.id.substringBefore(':') else ep.id
                ep.copy(id = "watch/$providerName/$anilistId/dub/$prefix-${ep.number}")
            }
            providerData.copy(
                episodes = providerData.episodes.copy(sub = newSub, dub = newDub)
            )
        }
        return data.copy(providers = newProviders)
    }
}
