package com.axiel7.anihyou.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.axiel7.anihyou.SeasonalAnimeQuery
import com.axiel7.anihyou.data.api.MediaApi
import com.axiel7.anihyou.data.model.media.AnimeSeason
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@AssistedFactory
fun interface AnimeSeasonalPagingSourceFactory {
    fun create(
        animeSeason: AnimeSeason,
    ): AnimeSeasonalPagingSource
}

class AnimeSeasonalPagingSource @AssistedInject constructor(
    private val mediaApi: MediaApi,
    @Assisted private val animeSeason: AnimeSeason,
) : PagingSource<Int, SeasonalAnimeQuery.Medium>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SeasonalAnimeQuery.Medium> {
        return try {
            val page = params.key ?: 1

            val data = mediaApi.seasonalAnimeQuery(
                animeSeason = animeSeason,
                page = page,
                perPage = params.loadSize
            ).execute().dataOrThrow()

            LoadResult.Page(
                data = data.Page?.media?.filterNotNull().orEmpty(),
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (data.Page?.pageInfo?.hasNextPage == true)
                    data.Page.pageInfo.currentPage?.plus(1)
                else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, SeasonalAnimeQuery.Medium>): Int? {
        return state.anchorPosition
    }
}