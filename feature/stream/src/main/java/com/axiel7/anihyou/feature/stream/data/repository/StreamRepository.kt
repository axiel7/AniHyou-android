package com.axiel7.anihyou.feature.stream.data.repository

import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.base.PagedResult
import com.axiel7.anihyou.core.domain.repository.MediaRepository
import com.axiel7.anihyou.core.model.media.currentAnimeSeason
import com.axiel7.anihyou.core.model.media.AnimeSeason
import com.axiel7.anihyou.core.network.type.MediaSeason
import com.axiel7.anihyou.core.network.fragment.CommonMediaListEntry
import com.axiel7.anihyou.core.network.MediaDetailsQuery
import com.axiel7.anihyou.core.network.MediaSortedQuery
import com.axiel7.anihyou.core.network.SeasonalAnimeQuery
import com.axiel7.anihyou.feature.stream.data.model.AnimeInfoResponse
import com.axiel7.anihyou.feature.stream.data.model.AnimeTitle
import com.axiel7.anihyou.feature.stream.data.model.CoverImage
import com.axiel7.anihyou.feature.stream.data.model.Episode
import com.axiel7.anihyou.feature.stream.data.model.EpisodeListResponse
import com.axiel7.anihyou.feature.stream.data.model.PagedAnimeResponse
import com.axiel7.anihyou.feature.stream.data.model.SpotlightResponse
import com.axiel7.anihyou.feature.stream.data.model.StreamAnime
import com.axiel7.anihyou.feature.stream.data.model.StreamSourcesResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.LocalDateTime

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
    private val mediaRepository: MediaRepository,
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
        val now = LocalDateTime.now()
        val season = now.currentAnimeSeason()
        val result = mediaRepository.getSeasonalAnimePage(
            animeSeason = season,
            sort = listOf(com.axiel7.anihyou.core.network.type.MediaSort.POPULARITY_DESC),
            isAdult = false,
            page = 1,
            perPage = 5
        ).first { it !is PagedResult.Loading }

        when (result) {
            is PagedResult.Success -> {
                val list = result.list.map { it.toStreamAnime() }
                DataResult.Success(SpotlightResponse(results = list))
            }
            is PagedResult.Error -> DataResult.Error(result.message)
            else -> DataResult.Error("Failed to fetch spotlight")
        }
    }.getOrElse { DataResult.Error(it.message ?: "spotlight failed") }

    suspend fun getTrending(page: Int = 1, perPage: Int = 20): DataResult<PagedAnimeResponse> =
        runCatching {
            val result = mediaRepository.getMediaSortedPage(
                mediaType = com.axiel7.anihyou.core.network.type.MediaType.ANIME,
                sort = listOf(com.axiel7.anihyou.core.network.type.MediaSort.TRENDING_DESC),
                isAdult = false,
                page = page,
                perPage = perPage
            ).first { it !is PagedResult.Loading }

            when (result) {
                is PagedResult.Success -> {
                    val list = result.list.map { it.toStreamAnime() }
                    DataResult.Success(
                        PagedAnimeResponse(
                            page = page,
                            perPage = perPage,
                            results = list
                        )
                    )
                }
                is PagedResult.Error -> DataResult.Error(result.message)
                else -> DataResult.Error("Failed to fetch trending")
            }
        }.getOrElse { DataResult.Error(it.message ?: "trending failed") }

    suspend fun getPopular(page: Int = 1, perPage: Int = 20): DataResult<PagedAnimeResponse> =
        runCatching {
            val result = mediaRepository.getMediaSortedPage(
                mediaType = com.axiel7.anihyou.core.network.type.MediaType.ANIME,
                sort = listOf(com.axiel7.anihyou.core.network.type.MediaSort.POPULARITY_DESC),
                isAdult = false,
                page = page,
                perPage = perPage
            ).first { it !is PagedResult.Loading }

            when (result) {
                is PagedResult.Success -> {
                    val list = result.list.map { it.toStreamAnime() }
                    DataResult.Success(
                        PagedAnimeResponse(
                            page = page,
                            perPage = perPage,
                            results = list
                        )
                    )
                }
                is PagedResult.Error -> DataResult.Error(result.message)
                else -> DataResult.Error("Failed to fetch popular")
            }
        }.getOrElse { DataResult.Error(it.message ?: "popular failed") }

    suspend fun getRecent(page: Int = 1, perPage: Int = 20): DataResult<PagedAnimeResponse> =
        runCatching {
            val now = LocalDateTime.now()
            val season = now.currentAnimeSeason()
            val result = mediaRepository.getSeasonalAnimePage(
                animeSeason = season,
                sort = listOf(com.axiel7.anihyou.core.network.type.MediaSort.START_DATE_DESC),
                isAdult = false,
                page = page,
                perPage = perPage
            ).first { it !is PagedResult.Loading }

            when (result) {
                is PagedResult.Success -> {
                    val list = result.list.map { it.toStreamAnime() }
                    DataResult.Success(
                        PagedAnimeResponse(
                            page = page,
                            perPage = perPage,
                            results = list
                        )
                    )
                }
                is PagedResult.Error -> DataResult.Error(result.message)
                else -> DataResult.Error("Failed to fetch recent")
            }
        }.getOrElse { DataResult.Error(it.message ?: "recent failed") }

    suspend fun getUpcoming(page: Int = 1, perPage: Int = 20): DataResult<PagedAnimeResponse> =
        runCatching {
            val now = LocalDateTime.now()
            val season = now.currentAnimeSeason()
            val result = mediaRepository.getSeasonalAnimePage(
                animeSeason = season, // Approximate upcoming via next/current seasonal
                sort = listOf(com.axiel7.anihyou.core.network.type.MediaSort.POPULARITY_DESC),
                isAdult = false,
                page = page,
                perPage = perPage
            ).first { it !is PagedResult.Loading }

            when (result) {
                is PagedResult.Success -> {
                    val list = result.list.map { it.toStreamAnime() }
                    DataResult.Success(
                        PagedAnimeResponse(
                            page = page,
                            perPage = perPage,
                            results = list
                        )
                    )
                }
                is PagedResult.Error -> DataResult.Error(result.message)
                else -> DataResult.Error("Failed to fetch upcoming")
            }
        }.getOrElse { DataResult.Error(it.message ?: "upcoming failed") }

    suspend fun getAnimeBySeason(
        season: MediaSeason,
        year: Int,
        page: Int = 1,
        perPage: Int = 20
    ): DataResult<PagedAnimeResponse> = runCatching {
        val result = mediaRepository.getSeasonalAnimePage(
            animeSeason = AnimeSeason(year = year, season = season),
            sort = listOf(com.axiel7.anihyou.core.network.type.MediaSort.POPULARITY_DESC),
            isAdult = false,
            page = page,
            perPage = perPage
        ).first { it !is PagedResult.Loading }

        when (result) {
            is PagedResult.Success -> {
                val list = result.list.map { it.toStreamAnime() }
                DataResult.Success(
                    PagedAnimeResponse(
                        page = page,
                        perPage = perPage,
                        results = list
                    )
                )
            }
            is PagedResult.Error -> DataResult.Error(result.message)
            else -> DataResult.Error("Failed to fetch seasonal anime")
        }
    }.getOrElse { DataResult.Error(it.message ?: "seasonal failed") }

    suspend fun search(
        query: String,
        page: Int = 1,
        perPage: Int = 20,
    ): DataResult<PagedAnimeResponse> = runCatching {
        val rawJson = pipe.pipeGet("search", mapOf("query" to query))
        val results = json.decodeFromString<List<StreamAnime>>(rawJson)
        DataResult.Success(
            PagedAnimeResponse(
                page = page,
                perPage = perPage,
                results = results
            )
        )
    }.getOrElse { DataResult.Error(it.message ?: "search failed") }

    // ── Anime info ────────────────────────────────────────────────────────────

    suspend fun getAnimeInfo(anilistId: Int): DataResult<AnimeInfoResponse> = runCatching {
        val result = mediaRepository.getMediaDetails(anilistId)
            .first { it !is DataResult.Loading }
        when (result) {
            is DataResult.Success -> {
                val media = result.data
                if (media != null) {
                    DataResult.Success(media.toAnimeInfoResponse())
                } else {
                    DataResult.Error("Anime not found")
                }
            }
            is DataResult.Error -> DataResult.Error(result.message)
            else -> DataResult.Error("Failed to load anime info")
        }
    }.getOrElse { DataResult.Error(it.message ?: "info failed") }

    // ── Mappers ───────────────────────────────────────────────────────────────

    fun CommonMediaListEntry.toStreamAnime(): StreamAnime {
        return StreamAnime(
            id = this.mediaId,
            title = AnimeTitle(
                english = this.media?.basicMediaDetails?.title?.userPreferred,
                romaji = this.media?.basicMediaDetails?.title?.userPreferred
            ),
            coverImage = CoverImage(
                large = this.media?.coverImage?.large
            ),
            format = this.media?.basicMediaDetails?.type?.rawValue,
            averageScore = null,
            genres = emptyList()
        )
    }

    private fun SeasonalAnimeQuery.Medium.toStreamAnime(): StreamAnime {
        return StreamAnime(
            id = this.basicMediaDetails.id,
            title = AnimeTitle(
                english = this.basicMediaDetails.title?.userPreferred,
                romaji = this.basicMediaDetails.title?.userPreferred
            ),
            coverImage = CoverImage(
                large = this.coverImage?.large
            ),
            bannerImage = this.bannerImage,
            format = this.format?.rawValue,
            seasonYear = this.seasonYear,
            averageScore = this.averageScore ?: this.meanScore,
            genres = this.genres?.filterNotNull() ?: emptyList()
        )
    }

    private fun MediaSortedQuery.Medium.toStreamAnime(): StreamAnime {
        return StreamAnime(
            id = this.basicMediaDetails.id,
            title = AnimeTitle(
                english = this.basicMediaDetails.title?.userPreferred,
                romaji = this.basicMediaDetails.title?.userPreferred
            ),
            coverImage = CoverImage(
                large = this.coverImage?.large
            ),
            bannerImage = this.bannerImage,
            format = this.format?.rawValue,
            seasonYear = this.seasonYear,
            averageScore = this.averageScore ?: this.meanScore,
            genres = this.genres?.filterNotNull() ?: emptyList()
        )
    }

    private fun MediaDetailsQuery.Media.toAnimeInfoResponse(): AnimeInfoResponse {
        return AnimeInfoResponse(
            id = this.basicMediaDetails.id,
            idMal = this.idMal,
            title = AnimeTitle(
                romaji = this.title?.romaji,
                english = this.title?.english,
                native = this.title?.native
            ),
            description = this.description,
            coverImage = CoverImage(
                large = this.coverImage?.large,
                extraLarge = this.coverImage?.extraLarge,
                color = this.coverImage?.color
            ),
            bannerImage = this.bannerImage,
            format = this.format?.rawValue,
            season = this.season?.rawValue,
            seasonYear = this.seasonYear,
            episodes = this.basicMediaDetails.episodes,
            duration = this.duration,
            status = this.status?.rawValue,
            averageScore = this.averageScore,
            meanScore = this.meanScore,
            popularity = this.popularity,
            favourites = this.favourites,
            genres = this.genres?.filterNotNull() ?: emptyList(),
            synonyms = this.synonyms?.filterNotNull() ?: emptyList(),
            siteUrl = this.siteUrl
        )
    }

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
