package com.axiel7.anihyou.core.domain.repository

import com.axiel7.anihyou.core.model.genre.GenresAndTags
import com.axiel7.anihyou.core.model.genre.SelectableGenre
import com.axiel7.anihyou.core.model.media.CountryOfOrigin
import com.axiel7.anihyou.core.network.api.CharacterApi
import com.axiel7.anihyou.core.network.api.MediaApi
import com.axiel7.anihyou.core.network.api.StaffApi
import com.axiel7.anihyou.core.network.api.StudioApi
import com.axiel7.anihyou.core.network.api.UserApi
import com.axiel7.anihyou.core.network.type.MediaFormat
import com.axiel7.anihyou.core.network.type.MediaSeason
import com.axiel7.anihyou.core.network.type.MediaSort
import com.axiel7.anihyou.core.network.type.MediaStatus
import com.axiel7.anihyou.core.network.type.MediaType

class SearchRepository(
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
        minimumTagPercentage: Int? = null,
        formatIn: List<MediaFormat>? = null,
        statusIn: List<MediaStatus>? = null,
        startYear: Int? = null,
        endYear: Int? = null,
        season: MediaSeason? = null,
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
            minimumTagPercentage,
            formatIn,
            statusIn,
            startYear,
            endYear,
            season,
            onList,
            isLicensed,
            isAdult,
            country?.toDto(),
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