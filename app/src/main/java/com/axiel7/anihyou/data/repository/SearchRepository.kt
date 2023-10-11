package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.cache.normalized.watch
import com.axiel7.anihyou.data.api.CharacterApi
import com.axiel7.anihyou.data.api.MediaApi
import com.axiel7.anihyou.data.api.StaffApi
import com.axiel7.anihyou.data.api.StudioApi
import com.axiel7.anihyou.data.api.UserApi
import com.axiel7.anihyou.data.model.GenresAndTags
import com.axiel7.anihyou.data.model.SelectableGenre
import com.axiel7.anihyou.data.model.asDataResult
import com.axiel7.anihyou.data.model.asPagedResult
import com.axiel7.anihyou.type.MediaFormat
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaStatus
import com.axiel7.anihyou.type.MediaType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    private val mediaApi: MediaApi,
    private val characterApi: CharacterApi,
    private val staffApi: StaffApi,
    private val studioApi: StudioApi,
    private val userApi: UserApi,
) {

    fun searchMedia(
        mediaType: MediaType,
        query: String,
        sort: List<MediaSort> = listOf(MediaSort.SEARCH_MATCH),
        genreIn: List<String>? = null,
        tagIn: List<String>? = null,
        formatIn: List<MediaFormat>? = null,
        statusIn: List<MediaStatus>? = null,
        year: Int? = null,
        onList: Boolean? = null,
        page: Int,
        perPage: Int = 25,
    ) = mediaApi
        .searchMediaQuery(
            mediaType,
            query,
            sort,
            genreIn,
            tagIn,
            formatIn,
            statusIn,
            year,
            onList,
            page,
            perPage
        )
        .watch()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) {
            it.Page?.media?.filterNotNull().orEmpty()
        }

    fun searchCharacter(
        query: String,
        page: Int = 1,
        perPage: Int = 25,
    ) = characterApi
        .searchCharacterQuery(query, page, perPage)
        .watch()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) {
            it.Page?.characters?.filterNotNull().orEmpty()
        }

    fun searchStaff(
        query: String,
        page: Int = 1,
        perPage: Int = 25,
    ) = staffApi
        .searchStaffQuery(query, page, perPage)
        .watch()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) {
            it.Page?.staff?.filterNotNull().orEmpty()
        }

    fun searchStudio(
        query: String,
        page: Int = 1,
        perPage: Int = 25,
    ) = studioApi
        .searchStudioQuery(query, page, perPage)
        .watch()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) {
            it.Page?.studios?.filterNotNull().orEmpty()
        }

    fun searchUser(
        query: String,
        page: Int = 1,
        perPage: Int = 25,
    ) = userApi
        .searchUserQuery(query, page, perPage)
        .watch()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) {
            it.Page?.users?.filterNotNull().orEmpty()
        }

    fun getGenreTagCollection() = mediaApi
        .genreTagCollectionQuery()
        .toFlow()
        .asDataResult { data ->
            GenresAndTags(
                genres = data.GenreCollection?.filterNotNull().orEmpty()
                    .map { SelectableGenre(it, false) },
                tags = data.MediaTagCollection?.filterNotNull()?.map { it.name }.orEmpty()
                    .map { SelectableGenre(it, false) }
            )
        }
}