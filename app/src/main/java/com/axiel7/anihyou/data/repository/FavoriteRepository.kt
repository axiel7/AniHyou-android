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
            animeId = if (animeId != null) Optional.present(animeId) else Optional.absent(),
            mangaId = if (mangaId != null) Optional.present(mangaId) else Optional.absent(),
            characterId = if (characterId != null) Optional.present(characterId) else Optional.absent(),
            staffId = if (staffId != null) Optional.present(staffId) else Optional.absent(),
            studioId = if (studioId != null) Optional.present(studioId) else Optional.absent(),
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