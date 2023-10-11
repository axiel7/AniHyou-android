package com.axiel7.anihyou.data.api

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.CharacterDetailsQuery
import com.axiel7.anihyou.CharacterMediaQuery
import com.axiel7.anihyou.SearchCharacterQuery
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterApi @Inject constructor(
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