package com.axiel7.anihyou.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.axiel7.anihyou.MediaChartQuery
import com.axiel7.anihyou.data.api.MediaApi
import com.axiel7.anihyou.data.model.media.ChartType
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@AssistedFactory
fun interface MediaChartPagingSourceFactory {
    fun create(
        type: ChartType,
    ): MediaChartPagingSource
}

class MediaChartPagingSource @AssistedInject constructor(
    private val mediaApi: MediaApi,
    @Assisted private val type: ChartType,
) : PagingSource<Int, MediaChartQuery.Medium>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaChartQuery.Medium> {
        return try {
            val page = params.key ?: 1

            val data = mediaApi.mediaChartQuery(
                type = type.mediaType,
                sort = listOf(type.mediaSort),
                status = type.mediaStatus,
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

    override fun getRefreshKey(state: PagingState<Int, MediaChartQuery.Medium>): Int? {
        return state.anchorPosition
    }
}