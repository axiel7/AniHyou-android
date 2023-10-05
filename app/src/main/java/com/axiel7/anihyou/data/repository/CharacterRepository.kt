package com.axiel7.anihyou.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.apollographql.apollo3.cache.normalized.watch
import com.axiel7.anihyou.data.api.CharacterApi
import com.axiel7.anihyou.data.paging.CharacterMediaPagingSourceFactory
import javax.inject.Inject

class CharacterRepository @Inject constructor(
    private val api: CharacterApi,
    private val characterMediaPagingSourceFactory: CharacterMediaPagingSourceFactory
) {

    fun getCharacterDetails(characterId: Int) = api
        .characterDetailsQuery(characterId)
        .watch()
        .asDataResult {
            it.Character
        }

    fun getCharacterMediaPage(
        characterId: Int
    ) = Pager(
        config = PagingConfig(pageSize = 25)
    ) {
        characterMediaPagingSourceFactory.create(characterId)
    }.flow
}