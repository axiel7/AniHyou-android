package com.axiel7.anihyou.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.axiel7.anihyou.AiringAnimesQuery
import com.axiel7.anihyou.data.api.MediaApi
import com.axiel7.anihyou.type.AiringSort
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@AssistedFactory
fun interface AiringAnimePagingSourceFactory {
    fun create(
        airingAtGreater: Long,
        airingAtLesser: Long,
        sort: List<AiringSort>
    ): AiringAnimePagingSource
}

class AiringAnimePagingSource @AssistedInject constructor(
    private val mediaApi: MediaApi,
    @Assisted private val airingAtGreater: Long,
    @Assisted private val airingAtLesser: Long,
    @Assisted private val sort: List<AiringSort>,
) : PagingSource<Int, AiringAnimesQuery.AiringSchedule>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AiringAnimesQuery.AiringSchedule> {
        return try {
            val page = params.key ?: 1
            val data = mediaApi.airingAnimesQuery(
                airingAtGreater = airingAtGreater,
                airingAtLesser = airingAtLesser,
                sort = sort,
                page = page,
                perPage = params.loadSize,
            ).execute().dataOrThrow()

            LoadResult.Page(
                data = data.Page?.airingSchedules?.filterNotNull().orEmpty(),
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (data.Page?.pageInfo?.hasNextPage == true)
                    data.Page.pageInfo.currentPage?.plus(1)
                else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, AiringAnimesQuery.AiringSchedule>): Int? {
        return state.anchorPosition
    }
}