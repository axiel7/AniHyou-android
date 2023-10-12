package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.cache.normalized.watch
import com.axiel7.anihyou.data.api.CharacterApi
import com.axiel7.anihyou.data.model.asDataResult
import com.axiel7.anihyou.data.model.asPagedResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterRepository @Inject constructor(
    private val api: CharacterApi,
) {

    fun getCharacterDetails(characterId: Int) = api
        .characterDetailsQuery(characterId)
        .watch()
        .asDataResult {
            it.Character
        }

    fun getCharacterMediaPage(
        characterId: Int,
        page: Int,
        perPage: Int = 25,
    ) = api
        .characterMediaQuery(characterId, page, perPage)
        .toFlow()
        .asPagedResult(page = { it.Character?.media?.pageInfo?.commonPage }) {
            it.Character?.media?.edges?.filterNotNull().orEmpty()
        }
}