package com.axiel7.anihyou.core.domain.repository

import com.apollographql.apollo.cache.normalized.watch
import com.axiel7.anihyou.core.network.CharacterDetailsQuery
import com.axiel7.anihyou.core.network.api.CharacterApi
import kotlin.collections.orEmpty

class CharacterRepository(
    private val api: CharacterApi,
    defaultPreferencesRepository: DefaultPreferencesRepository,
) : BaseNetworkRepository(defaultPreferencesRepository) {

    fun getCharacterDetails(characterId: Int) = api
        .characterDetailsQuery(characterId)
        .watch()
        .asDataResult {
            it.Character
        }

    suspend fun updateCharacterDetailsCache(details: CharacterDetailsQuery.Character) {
        api.updateCharacterDetailsCache(
            data = CharacterDetailsQuery.Data(details)
        )
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