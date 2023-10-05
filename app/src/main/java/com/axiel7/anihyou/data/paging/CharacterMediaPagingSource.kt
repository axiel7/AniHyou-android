package com.axiel7.anihyou.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.axiel7.anihyou.CharacterMediaQuery
import com.axiel7.anihyou.data.api.CharacterApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@AssistedFactory
fun interface CharacterMediaPagingSourceFactory {
    fun create(
        characterId: Int,
    ): CharacterMediaPagingSource
}

class CharacterMediaPagingSource @AssistedInject constructor(
    private val characterApi: CharacterApi,
    @Assisted private val characterId: Int,
) : PagingSource<Int, CharacterMediaQuery.Edge>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterMediaQuery.Edge> {
        return try {
            val page = params.key ?: 1
            val data = characterApi.characterMediaQuery(
                characterId = characterId,
                page = page,
                perPage = params.loadSize
            ).execute().dataOrThrow()

            LoadResult.Page(
                data = data.Character?.media?.edges?.filterNotNull().orEmpty(),
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (data.Character?.media?.pageInfo?.hasNextPage == true)
                    data.Character.media.pageInfo.currentPage?.plus(1)
                else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CharacterMediaQuery.Edge>): Int? {
        return state.anchorPosition
    }
}