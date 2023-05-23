package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.UserCurrentAnimeListQuery
import com.axiel7.anihyou.network.apolloClient
import com.axiel7.anihyou.type.MediaStatus

object MediaRepository {

    suspend fun getUserCurrentAiringAnime(userId: Int): List<UserCurrentAnimeListQuery.MediaList>? {
        val response = apolloClient.query(UserCurrentAnimeListQuery(
            userId = Optional.present(userId)
        )).execute()
        if (response.hasErrors()) return null
        else {
            response.data?.Page?.mediaList?.filterNotNull()?.let { mediaList ->
                return mediaList
                    .filter { it.media?.status == MediaStatus.RELEASING }
                    .sortedBy { it.media?.nextAiringEpisode?.timeUntilAiring }
            }
            return null
        }
    }
}