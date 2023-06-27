package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.ToggleFavouriteMutation
import com.axiel7.anihyou.data.repository.BaseRepository.getError
import com.axiel7.anihyou.data.repository.BaseRepository.tryMutation
import com.axiel7.anihyou.ui.base.UiState
import kotlinx.coroutines.flow.flow

object FavoriteRepository {

    fun toggleFavorite(
        animeId: Int? = null,
        mangaId: Int? = null,
        characterId: Int? = null,
        staffId: Int? = null,
        studioId: Int? = null,
    ) = flow {
        emit(UiState.Loading)
        val response = ToggleFavouriteMutation(
            animeId = Optional.presentIfNotNull(animeId),
            mangaId = Optional.presentIfNotNull(mangaId),
            characterId = Optional.presentIfNotNull(characterId),
            staffId = Optional.presentIfNotNull(staffId),
            studioId = Optional.presentIfNotNull(studioId),
        ).tryMutation()

        val error = response.getError()
        if (error != null) emit(UiState.Error(message = error))
        else {
            val favorite = response?.data?.ToggleFavourite
            if (favorite != null) emit(UiState.Success(true))
            else emit(UiState.Error(message = "Error"))
        }
    }
}