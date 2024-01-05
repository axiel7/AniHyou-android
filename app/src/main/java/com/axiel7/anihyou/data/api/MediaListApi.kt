package com.axiel7.anihyou.data.api

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.axiel7.anihyou.DeleteMediaListMutation
import com.axiel7.anihyou.MediaListCustomListsQuery
import com.axiel7.anihyou.UpdateEntryMutation
import com.axiel7.anihyou.UpdateEntryProgressMutation
import com.axiel7.anihyou.UserMediaListQuery
import com.axiel7.anihyou.type.FuzzyDateInput
import com.axiel7.anihyou.type.MediaListSort
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaListApi @Inject constructor(
    private val client: ApolloClient
) {
    fun userMediaListQuery(
        userId: Int,
        mediaType: MediaType,
        status: MediaListStatus,
        sort: List<MediaListSort>,
        fetchFromNetwork: Boolean,
        page: Int,
        perPage: Int,
    ) = client
        .query(
            UserMediaListQuery(
                page = Optional.present(page),
                perPage = Optional.present(perPage),
                userId = Optional.present(userId),
                type = Optional.present(mediaType),
                status = Optional.present(status),
                sort = Optional.present(sort)
            )
        )
        .fetchPolicy(if (fetchFromNetwork) FetchPolicy.NetworkFirst else FetchPolicy.CacheFirst)

    fun updateEntryProgressMutation(
        entryId: Int,
        progress: Int
    ) = client
        .mutation(
            UpdateEntryProgressMutation(
                saveMediaListEntryId = Optional.present(entryId),
                progress = Optional.present(progress)
            )
        )

    fun updateEntryMutation(
        mediaId: Int,
        status: MediaListStatus?,
        score: Double?,
        advancedScores: List<Double>?,
        progress: Int?,
        progressVolumes: Int?,
        startedAt: FuzzyDateInput?,
        completedAt: FuzzyDateInput?,
        repeat: Int?,
        private: Boolean?,
        notes: String?,
        customLists: List<String?>?,
    ) = client
        .mutation(
            UpdateEntryMutation(
                mediaId = Optional.present(mediaId),
                status = Optional.presentIfNotNull(status),
                score = Optional.presentIfNotNull(score),
                advancedScores = Optional.presentIfNotNull(advancedScores),
                progress = Optional.presentIfNotNull(progress),
                progressVolumes = Optional.presentIfNotNull(progressVolumes),
                startedAt = Optional.presentIfNotNull(startedAt),
                completedAt = Optional.presentIfNotNull(completedAt),
                repeat = Optional.presentIfNotNull(repeat),
                private = Optional.presentIfNotNull(private),
                notes = Optional.presentIfNotNull(notes),
                customLists = Optional.presentIfNotNull(customLists),
            )
        )

    fun deleteMediaListMutation(id: Int) = client
        .mutation(
            DeleteMediaListMutation(
                mediaListEntryId = Optional.present(id)
            )
        )

    fun mediaListCustomLists(
        id: Int,
        userId: Int
    ) = client
        .query(
            MediaListCustomListsQuery(
                id = Optional.present(id),
                userId = Optional.present(userId)
            )
        )
}