package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.CharacterDetailsQuery
import com.axiel7.anihyou.CharacterMediaQuery
import com.axiel7.anihyou.data.repository.BaseRepository.getError
import com.axiel7.anihyou.data.repository.BaseRepository.tryQuery
import kotlinx.coroutines.flow.flow

object CharacterRepository {

    fun getCharacterDetails(characterId: Int) = flow {
        emit(DataResult.Loading)
        val response = CharacterDetailsQuery(
            characterId = Optional.present(characterId)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(DataResult.Error(message = error))
        else {
            val character = response?.data?.Character
            if (character != null) emit(DataResult.Success(data = character))
            else emit(DataResult.Error(message = "Error"))
        }
    }

    fun getCharacterMediaPage(
        characterId: Int,
        page: Int = 1,
        perPage: Int = 25,
    ) = flow {
        emit(PagedResult.Loading)

        val response = CharacterMediaQuery(
            characterId = Optional.present(characterId),
            page = Optional.present(page),
            perPage = Optional.present(perPage)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(PagedResult.Error(message = error))
        else {
            val mediaPage = response?.data?.Character?.media
            if (mediaPage != null) emit(PagedResult.Success(
                data = mediaPage.edges?.filterNotNull().orEmpty(),
                nextPage = if (mediaPage.pageInfo?.hasNextPage == true)
                    mediaPage.pageInfo.currentPage?.plus(1)
                else null
            ))
            else emit(PagedResult.Error(message = "Error"))
        }
    }
}