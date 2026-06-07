package com.axiel7.anihyou.core.streaming.repository

import com.axiel7.anihyou.core.streaming.api.StreamingProvider
import com.axiel7.anihyou.core.streaming.model.EpisodeList
import com.axiel7.anihyou.core.streaming.model.PlaybackInfo
import com.axiel7.anihyou.core.streaming.model.StreamingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StreamingRepository(
    private val providers: Map<StreamingSource, StreamingProvider>,
    private val preferencesRepository: StreamingPreferencesRepository,
) {
    fun getEpisodes(
        anilistId: Int,
        title: String,
        isDub: Boolean,
        source: StreamingSource = StreamingSource.ALL_ANIME,
    ): Flow<StreamResult<EpisodeList>> = flow {
        emit(StreamResult.Loading)
        val provider = providers[source] ?: run {
            emit(StreamResult.Error("Source not available"))
            return@flow
        }
        // Cache the source-specific anime ID to avoid repeated searches
        val cachedId = preferencesRepository.getAnimeSourceId(anilistId, source)
        val animeId = cachedId ?: provider.findAnimeId(anilistId, title).also { id ->
            if (id != null) preferencesRepository.saveAnimeSourceId(anilistId, source, id)
        }
        if (animeId == null) {
            emit(StreamResult.Error("Anime not found on ${provider.name}"))
            return@flow
        }
        val episodes = provider.getEpisodes(animeId, isDub)
        emit(StreamResult.Success(episodes))
    }

    fun getPlaybackInfo(
        episodeId: String,
        source: StreamingSource = StreamingSource.ALL_ANIME,
    ): Flow<StreamResult<PlaybackInfo>> = flow {
        emit(StreamResult.Loading)
        val provider = providers[source] ?: run {
            emit(StreamResult.Error("Source not available"))
            return@flow
        }
        val info = provider.getPlaybackInfo(episodeId)
        if (info.sources.isEmpty()) {
            emit(StreamResult.Error("No streams found for this episode"))
        } else {
            emit(StreamResult.Success(info))
        }
    }
}

sealed class StreamResult<out T> {
    data object Loading : StreamResult<Nothing>()
    data class Success<T>(val data: T) : StreamResult<T>()
    data class Error(val message: String) : StreamResult<Nothing>()
}
