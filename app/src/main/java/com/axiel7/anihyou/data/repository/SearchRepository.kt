package com.axiel7.anihyou.data.repository

import com.axiel7.anihyou.data.api.CharacterApi
import com.axiel7.anihyou.data.api.MediaApi
import com.axiel7.anihyou.data.api.StaffApi
import com.axiel7.anihyou.data.api.StudioApi
import com.axiel7.anihyou.data.api.UserApi
import com.axiel7.anihyou.data.model.genre.GenresAndTags
import com.axiel7.anihyou.data.model.genre.SelectableGenre
import com.axiel7.anihyou.data.model.media.CountryOfOrigin
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
    defaultPreferencesRepository: DefaultPreferencesRepository,
) : BaseNetworkRepository(defaultPreferencesRepository) {

    fun searchMedia(
        mediaType: MediaType,
        query: String,
        sort: List<MediaSort> = listOf(MediaSort.SEARCH_MATCH),
        genreIn: List<String>? = null,
        genreNotIn: List<String>? = null,
        tagIn: List<String>? = null,
        tagNotIn: List<String>? = null,
        formatIn: List<MediaFormat>? = null,
        statusIn: List<MediaStatus>? = null,
        startYear: Int? = null,
        endYear: Int? = null,
        onList: Boolean? = null,
        isLicensed: Boolean? = null,
        isAdult: Boolean? = null,
        country: CountryOfOrigin? = null,
        page: Int,
        perPage: Int = 25,
    ) = mediaApi
        .searchMediaQuery(
            mediaType,
            query,
            sort,
            genreIn,
            genreNotIn,
            tagIn,
            tagNotIn,
            formatIn,
            statusIn,
            startYear,
            endYear,
            onList,
            isLicensed,
            isAdult,
            country,
            page,
            perPage
        )
        .toFlow()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) {
            it.Page?.media?.filterNotNull().orEmpty()
        }

    fun searchCharacter(
        query: String,
        page: Int = 1,
        perPage: Int = 25,
    ) = characterApi
        .searchCharacterQuery(query, page, perPage)
        .toFlow()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) {
            it.Page?.characters?.filterNotNull().orEmpty()
        }

    fun searchStaff(
        query: String,
        page: Int = 1,
        perPage: Int = 25,
    ) = staffApi
        .searchStaffQuery(query, page, perPage)
        .toFlow()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) {
            it.Page?.staff?.filterNotNull().orEmpty()
        }

    fun searchStudio(
        query: String,
        page: Int = 1,
        perPage: Int = 25,
    ) = studioApi
        .searchStudioQuery(query, page, perPage)
        .toFlow()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) {
            it.Page?.studios?.filterNotNull().orEmpty()
        }

    fun searchUser(
        query: String,
        page: Int = 1,
        perPage: Int = 25,
    ) = userApi
        .searchUserQuery(query, page, perPage)
        .toFlow()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) {
            it.Page?.users?.filterNotNull().orEmpty()
        }

    fun getGenreTagCollection() = mediaApi
        .genreTagCollectionQuery()
        .toFlow()
        .asDataResult { data ->
            GenresAndTags(
                genres = data.GenreCollection?.filterNotNull().orEmpty()
                    .map { SelectableGenre(it) },
                tags = data.MediaTagCollection?.filterNotNull()?.map { it.name }.orEmpty()
                    .map { SelectableGenre(it) }
            )
        }
}