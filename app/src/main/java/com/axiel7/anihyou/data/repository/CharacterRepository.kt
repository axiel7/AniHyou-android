package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.CharacterDetailsQuery
import com.axiel7.anihyou.CharacterMediaQuery
import com.axiel7.anihyou.data.repository.BaseRepository.getError
import com.axiel7.anihyou.data.repository.BaseRepository.tryQuery
import com.axiel7.anihyou.ui.base.UiState
import kotlinx.coroutines.flow.flow

object CharacterRepository {

    fun getCharacterDetails(characterId: Int) = flow {
        emit(UiState.Loading)
        val response = CharacterDetailsQuery(
            characterId = Optional.present(characterId)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(UiState.Error(message = error))
        else {
            val character = response?.data?.Character
            if (character != null) emit(UiState.Success(data = character))
            else emit(UiState.Error(message = "Error"))
        }
    }

    fun getCharacterMediaPage(
        characterId: Int,
        page: Int = 1,
        perPage: Int = 25,
    ) = flow {
        emit(UiState.Loading)

        val response = CharacterMediaQuery(
            characterId = Optional.present(characterId),
            page = Optional.present(page),
            perPage = Optional.present(perPage)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(UiState.Error(message = error))
        else {
            val mediaPage = response?.data?.Character?.media
            if (mediaPage != null) emit(UiState.Success(data = mediaPage))
            else emit(UiState.Error(message = "Error"))
        }
    }
}