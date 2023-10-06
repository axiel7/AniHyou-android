package com.axiel7.anihyou.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.axiel7.anihyou.ActivityFeedQuery
import com.axiel7.anihyou.data.api.ActivityApi
import com.axiel7.anihyou.data.model.activity.ActivityTypeGrouped
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@AssistedFactory
fun interface ActivityFeedPagingSourceFactory {
    fun create(
        isFollowing: Boolean,
        type: ActivityTypeGrouped?,
        refreshCache: Boolean,
    ): ActivityFeedPagingSource
}

class ActivityFeedPagingSource @AssistedInject constructor(
    private val activityApi: ActivityApi,
    @Assisted private val isFollowing: Boolean,
    @Assisted private val type: ActivityTypeGrouped?,
    @Assisted private val refreshCache: Boolean,
) : PagingSource<Int, ActivityFeedQuery.Activity>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ActivityFeedQuery.Activity> {
        return try {
            val page = params.key ?: 1

            val data = activityApi.activityFeedQuery(
                isFollowing = isFollowing,
                type = type,
                refreshCache = refreshCache,
                page = page,
                perPage = params.loadSize
            ).execute().dataOrThrow()

            LoadResult.Page(
                data = data.Page?.activities?.filterNotNull().orEmpty(),
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (data.Page?.pageInfo?.hasNextPage == true)
                    data.Page.pageInfo.currentPage?.plus(1)
                else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ActivityFeedQuery.Activity>): Int? {
        return state.anchorPosition
    }
}