package com.axiel7.anihyou.data.api

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.cache.normalized.apolloStore
import com.axiel7.anihyou.AiringAnimesQuery
import com.axiel7.anihyou.AiringOnMyListQuery
import com.axiel7.anihyou.AiringWidgetQuery
import com.axiel7.anihyou.GenreTagCollectionQuery
import com.axiel7.anihyou.MediaActivityQuery
import com.axiel7.anihyou.MediaCharactersAndStaffQuery
import com.axiel7.anihyou.MediaChartQuery
import com.axiel7.anihyou.MediaDetailsQuery
import com.axiel7.anihyou.MediaFollowingQuery
import com.axiel7.anihyou.MediaRelationsAndRecommendationsQuery
import com.axiel7.anihyou.MediaReviewsQuery
import com.axiel7.anihyou.MediaSortedQuery
import com.axiel7.anihyou.MediaStatsQuery
import com.axiel7.anihyou.MediaThreadsQuery
import com.axiel7.anihyou.SearchMediaQuery
import com.axiel7.anihyou.SeasonalAnimeQuery
import com.axiel7.anihyou.data.model.media.AnimeSeason
import com.axiel7.anihyou.data.model.media.CountryOfOrigin
import com.axiel7.anihyou.type.AiringSort
import com.axiel7.anihyou.type.MediaFormat
import com.axiel7.anihyou.type.MediaSeason
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.type.ThreadSort
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaApi @Inject constructor(
    private val client: ApolloClient
) {
    fun searchMediaQuery(
        mediaType: MediaType,
        query: String,
        sort: List<MediaSort>,
        genreIn: List<String>?,
        genreNotIn: List<String>?,
        tagIn: List<String>?,
        tagNotIn: List<String>?,
        formatIn: List<MediaFormat>?,
        statusIn: List<MediaStatus>?,
        startYear: Int?,
        endYear: Int?,
        season: MediaSeason?,
        onList: Boolean?,
        isLicensed: Boolean?,
        isAdult: Boolean?,
        country: CountryOfOrigin?,
        page: Int,
        perPage: Int,
    ) = client
        .query(
            SearchMediaQuery(
                page = Optional.present(page),
                perPage = Optional.present(perPage),
                search = if (query.isNotBlank()) Optional.present(query) else Optional.absent(),
                type = Optional.present(mediaType),
                sort = Optional.present(sort),
                genre_in = if (genreIn.isNullOrEmpty()) Optional.absent()
                else Optional.present(genreIn),
                genre_not_in = if (genreNotIn.isNullOrEmpty()) Optional.absent()
                else Optional.present(genreNotIn),
                tag_in = if (tagIn.isNullOrEmpty()) Optional.absent()
                else Optional.present(tagIn),
                tag_not_in = if (tagNotIn.isNullOrEmpty()) Optional.absent()
                else Optional.present(tagNotIn),
                format_in = if (formatIn.isNullOrEmpty()) Optional.absent()
                else Optional.present(formatIn),
                status_in = if (statusIn.isNullOrEmpty()) Optional.absent()
                else Optional.present(statusIn),
                startDateGreater = if (startYear != null)
                // Unknown dates represented by 0. E.g. 2016: 20160000
                    Optional.present(startYear * 10000)
                else Optional.absent(),
                startDateLesser = if (endYear != null)
                    Optional.present(endYear * 10000)
                else Optional.absent(),
                season = Optional.presentIfNotNull(season),
                onList = Optional.presentIfNotNull(onList),
                isLicensed = Optional.presentIfNotNull(isLicensed),
                isAdult = Optional.presentIfNotNull(isAdult),
                country = Optional.presentIfNotNull(country),
            )
        )

    fun genreTagCollectionQuery() = client.query(GenreTagCollectionQuery())

    fun airingAnimesQuery(
        airingAtGreater: Long?,
        airingAtLesser: Long?,
        sort: List<AiringSort>,
        page: Int,
        perPage: Int,
    ) = client
        .query(
            AiringAnimesQuery(
                page = Optional.present(page),
                perPage = Optional.present(perPage),
                sort = Optional.present(sort),
                airingAtGreater = Optional.presentIfNotNull(airingAtGreater?.toInt()),
                airingAtLesser = Optional.presentIfNotNull(airingAtLesser?.toInt()),
            )
        )

    fun airingOnMyListQuery(
        page: Int,
        perPage: Int,
    ) = client
        .query(
            AiringOnMyListQuery(
                page = Optional.present(page),
                perPage = Optional.present(perPage)
            )
        )

    fun airingWidgetQuery(
        page: Int,
        perPage: Int,
    ) = client
        .query(
            AiringWidgetQuery(
                page = Optional.present(page),
                perPage = Optional.present(perPage)
            )
        )

    fun seasonalAnimeQuery(
        animeSeason: AnimeSeason,
        sort: List<MediaSort>,
        page: Int,
        perPage: Int,
    ) = client
        .query(
            SeasonalAnimeQuery(
                page = Optional.present(page),
                perPage = Optional.present(perPage),
                season = Optional.present(animeSeason.season),
                seasonYear = Optional.present(animeSeason.year),
                sort = Optional.present(sort)
            )
        )

    fun mediaSortedQuery(
        mediaType: MediaType,
        sort: List<MediaSort>,
        page: Int,
        perPage: Int,
    ) = client
        .query(
            MediaSortedQuery(
                page = Optional.present(page),
                perPage = Optional.present(perPage),
                type = Optional.present(mediaType),
                sort = Optional.present(sort)
            )
        )

    fun mediaDetailsQuery(mediaId: Int) = client
        .query(
            MediaDetailsQuery(
                mediaId = Optional.present(mediaId)
            )
        )

    suspend fun updateMediaDetailsCache(data: MediaDetailsQuery.Data) {
        data.Media?.id?.let { mediaId ->
            try {
                client.apolloStore
                    .writeOperation(
                        operation = MediaDetailsQuery(
                            mediaId = Optional.present(mediaId)
                        ),
                        operationData = data,
                    )
            } catch (_: IllegalStateException) {
            }
        }
    }

    fun mediaCharactersAndStaffQuery(mediaId: Int) = client
        .query(
            MediaCharactersAndStaffQuery(
                mediaId = Optional.present(mediaId)
            )
        )

    fun mediaRelationsAndRecommendationsQuery(mediaId: Int) = client
        .query(
            MediaRelationsAndRecommendationsQuery(
                mediaId = Optional.present(mediaId)
            )
        )

    fun mediaStatsQuery(mediaId: Int) = client
        .query(
            MediaStatsQuery(
                mediaId = Optional.present(mediaId)
            )
        )

    fun mediaFollowingQuery(
        mediaId: Int,
        page: Int,
        perPage: Int,
    ) = client
        .query(
            MediaFollowingQuery(
                id = Optional.present(mediaId),
                page = Optional.present(page),
                perPage = Optional.present(perPage)
            )
        )

    fun mediaReviewsQuery(
        mediaId: Int,
        page: Int,
        perPage: Int,
    ) = client
        .query(
            MediaReviewsQuery(
                mediaId = Optional.present(mediaId),
                page = Optional.present(page),
                perPage = Optional.present(perPage)
            )
        )

    fun mediaThreadsQuery(
        mediaId: Int,
        page: Int,
        perPage: Int,
    ) = client
        .query(
            MediaThreadsQuery(
                page = Optional.present(page),
                perPage = Optional.present(perPage),
                mediaCategoryId = Optional.present(mediaId),
                sort = Optional.present(listOf(ThreadSort.CREATED_AT_DESC))
            )
        )

    fun mediaActivityQuery(
        mediaId: Int,
        userId: Int?,
        page: Int,
        perPage: Int,
    ) = client
        .query(
            MediaActivityQuery(
                mediaId = Optional.present(mediaId),
                userId = Optional.presentIfNotNull(userId),
                page = Optional.present(page),
                perPage = Optional.present(perPage),
            )
        )

    fun mediaChartQuery(
        type: MediaType,
        sort: List<MediaSort>,
        status: MediaStatus?,
        format: MediaFormat?,
        page: Int,
        perPage: Int,
    ) = client
        .query(
            MediaChartQuery(
                page = Optional.present(page),
                perPage = Optional.present(perPage),
                sort = Optional.present(sort),
                type = Optional.present(type),
                status = Optional.presentIfNotNull(status),
                format = Optional.presentIfNotNull(format),
            )
        )
}