package com.axiel7.anihyou.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.axiel7.anihyou.SearchMediaQuery
import com.axiel7.anihyou.data.api.MediaApi
import com.axiel7.anihyou.type.MediaFormat
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaStatus
import com.axiel7.anihyou.type.MediaType
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@AssistedFactory
fun interface SearchMediaPagingSourceFactory {
    fun create(
        mediaType: MediaType,
        query: String,
        sort: List<MediaSort>,
        genreIn: List<String>?,
        tagIn: List<String>?,
        formatIn: List<MediaFormat>?,
        statusIn: List<MediaStatus>?,
        year: Int?,
        onList: Boolean?,
    ): SearchMediaPagingSource
}

class SearchMediaPagingSource @AssistedInject constructor(
    private val mediaApi: MediaApi,
    @Assisted private val mediaType: MediaType,
    @Assisted private val query: String,
    @Assisted private val sort: List<MediaSort>,
    @Assisted private val genreIn: List<String>?,
    @Assisted private val tagIn: List<String>?,
    @Assisted private val formatIn: List<MediaFormat>?,
    @Assisted private val statusIn: List<MediaStatus>?,
    @Assisted private val year: Int?,
    @Assisted private val onList: Boolean?,
) : PagingSource<Int, SearchMediaQuery.Medium>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchMediaQuery.Medium> {
        return try {
            val page = params.key ?: 1

            val data = mediaApi.searchMediaQuery(
                mediaType = mediaType,
                query = query,
                sort = sort,
                genreIn = genreIn,
                tagIn = tagIn,
                formatIn = formatIn,
                statusIn = statusIn,
                year = year,
                onList = onList,
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

    override fun getRefreshKey(state: PagingState<Int, SearchMediaQuery.Medium>): Int? {
        return state.anchorPosition
    }
}