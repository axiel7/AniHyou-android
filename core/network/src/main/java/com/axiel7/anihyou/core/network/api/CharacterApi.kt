package com.axiel7.anihyou.core.network.api

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.cache.normalized.apolloStore
import com.axiel7.anihyou.core.network.CharacterDetailsQuery
import com.axiel7.anihyou.core.network.CharacterMediaQuery
import com.axiel7.anihyou.core.network.SearchCharacterQuery

class CharacterApi(
    private val client: ApolloClient
) {
    fun searchCharacterQuery(
        query: String,
        page: Int,
        perPage: Int,
    ) = client
        .query(
            SearchCharacterQuery(
                page = Optional.present(page),
                perPage = Optional.present(perPage),
                search = Optional.present(query)
            )
        )

    fun characterDetailsQuery(characterId: Int) = client
        .query(
            CharacterDetailsQuery(
                characterId = Optional.present(characterId)
            )
        )

    suspend fun updateCharacterDetailsCache(data: CharacterDetailsQuery.Data) {
        client.apolloStore
            .writeOperation(
                operation = CharacterDetailsQuery(
                    characterId = Optional.presentIfNotNull(data.Character?.id)
                ),
                operationData = data
            )
    }

    fun characterMediaQuery(
        characterId: Int,
        page: Int,
        perPage: Int,
    ) = client
        .query(
            CharacterMediaQuery(
                characterId = Optional.present(characterId),
                page = Optional.present(page),
                perPage = Optional.present(perPage)
            )
        )
}