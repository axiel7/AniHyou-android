package com.axiel7.anihyou.data.api

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.AiringAnimesQuery
import com.axiel7.anihyou.AiringOnMyListQuery
import com.axiel7.anihyou.GenreTagCollectionQuery
import com.axiel7.anihyou.MediaCharactersAndStaffQuery
import com.axiel7.anihyou.MediaChartQuery
import com.axiel7.anihyou.MediaDetailsQuery
import com.axiel7.anihyou.MediaRelationsAndRecommendationsQuery
import com.axiel7.anihyou.MediaReviewsQuery
import com.axiel7.anihyou.MediaSortedQuery
import com.axiel7.anihyou.MediaStatsQuery
import com.axiel7.anihyou.MediaThreadsQuery
import com.axiel7.anihyou.SearchMediaQuery
import com.axiel7.anihyou.SeasonalAnimeQuery
import com.axiel7.anihyou.UserCurrentAnimeListQuery
import com.axiel7.anihyou.data.model.media.AnimeSeason
import com.axiel7.anihyou.type.AiringSort
import com.axiel7.anihyou.type.MediaFormat
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.type.ThreadSort
import javax.inject.Inject

class MediaApi @Inject constructor(
    private val client: ApolloClient
) {
    fun searchMediaQuery(
        mediaType: MediaType,
        query: String,
        sort: List<MediaSort>,
        genreIn: List<String>?,
        tagIn: List<String>?,
        formatIn: List<MediaFormat>?,
        statusIn: List<MediaStatus>?,
        year: Int?,
        onList: Boolean?,
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
                tag_in = if (tagIn.isNullOrEmpty()) Optional.absent()
                else Optional.present(tagIn),
                format_in = if (formatIn.isNullOrEmpty()) Optional.absent()
                else Optional.present(formatIn),
                status_in = if (statusIn.isNullOrEmpty()) Optional.absent()
                else Optional.present(statusIn),
                seasonYear = Optional.presentIfNotNull(year),
                onList = if (onList == true) Optional.present(true) else Optional.absent()
            )
        )

    fun genreTagCollectionQuery() = client.query(GenreTagCollectionQuery())

    fun airingAnimesQuery(
        airingAtGreater: Long? = null,
        airingAtLesser: Long? = null,
        sort: List<AiringSort> = listOf(AiringSort.TIME),
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
    /*.watch()
    .asDataResult { data ->
        PageResult(
            list = data.Page?.media?.filterNotNull()
                ?.filter { it.nextAiringEpisode != null }
                ?.sortedBy { it.nextAiringEpisode?.timeUntilAiring }
                .orEmpty(),
            nextPage = if (data.Page?.pageInfo?.hasNextPage == true)
                data.Page.pageInfo.currentPage?.plus(1)
            else null
        )
    }*/

    fun seasonalAnimeQuery(
        animeSeason: AnimeSeason,
        page: Int,
        perPage: Int,
    ) = client
        .query(
            SeasonalAnimeQuery(
                page = Optional.present(page),
                perPage = Optional.present(perPage),
                season = Optional.present(animeSeason.season),
                seasonYear = Optional.present(animeSeason.year),
                sort = Optional.present(listOf(MediaSort.POPULARITY_DESC))
            )
        )
    /*.watch()
    .asDataResult {
        PageResult(
            list = it.Page?.media?.filterNotNull().orEmpty(),
            nextPage = if (it.Page?.pageInfo?.hasNextPage == true)
                it.Page.pageInfo.currentPage?.plus(1)
            else null
        )
    }*/

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
    /*.watch()
    .asDataResult {
        PageResult(
            list = it.Page?.media?.filterNotNull().orEmpty(),
            nextPage = if (it.Page?.pageInfo?.hasNextPage == true)
                it.Page.pageInfo.currentPage?.plus(1)
            else null
        )
    }*/

    fun mediaDetailsQuery(mediaId: Int) = client
        .query(
            MediaDetailsQuery(
                mediaId = Optional.present(mediaId)
            )
        )

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
    /*.watch()
    .asDataResult {
        PageResult(
            list = it.Media?.reviews?.nodes?.filterNotNull().orEmpty(),
            nextPage = if (it.Media?.reviews?.pageInfo?.hasNextPage == true)
                it.Media.reviews.pageInfo.currentPage?.plus(1)
            else null
        )
    }*/

    fun mediaThreadsQuery(
        mediaId: Int,
        page: Int = 1,
        perPage: Int = 25,
    ) = client
        .query(
            MediaThreadsQuery(
                page = Optional.present(page),
                perPage = Optional.present(perPage),
                mediaCategoryId = Optional.present(mediaId),
                sort = Optional.present(listOf(ThreadSort.CREATED_AT_DESC))
            )
        )
    /*.watch()
    .asDataResult {
        PageResult(
            list = it.Page?.threads?.filterNotNull().orEmpty(),
            nextPage = if (it.Page?.pageInfo?.hasNextPage == true)
                it.Page.pageInfo.currentPage?.plus(1)
            else null
        )
    }*/

    fun mediaChartQuery(
        type: MediaType,
        sort: List<MediaSort> = listOf(MediaSort.ID),
        status: MediaStatus? = null,
        page: Int,
        perPage: Int,
    ) = client
        .query(
            MediaChartQuery(
                page = Optional.present(page),
                perPage = Optional.present(perPage),
                sort = Optional.present(sort),
                type = Optional.present(type),
                status = Optional.presentIfNotNull(status)
            )
        )
    /*.watch()
    .asDataResult {
        PageResult(
            list = it.Page?.media?.filterNotNull().orEmpty(),
            nextPage = if (it.Page?.pageInfo?.hasNextPage == true)
                it.Page.pageInfo.currentPage?.plus(1)
            else null
        )
    }*/

    fun userCurrentAnimeListQuery(userId: Int) = client
        .query(
            UserCurrentAnimeListQuery(
                userId = Optional.present(userId)
            )
        )
}