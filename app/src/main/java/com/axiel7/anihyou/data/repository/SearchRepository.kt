package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.GenreTagCollectionQuery
import com.axiel7.anihyou.SearchCharacterQuery
import com.axiel7.anihyou.SearchMediaQuery
import com.axiel7.anihyou.SearchStaffQuery
import com.axiel7.anihyou.SearchStudioQuery
import com.axiel7.anihyou.SearchUserQuery
import com.axiel7.anihyou.data.model.GenresAndTags
import com.axiel7.anihyou.data.repository.BaseRepository.getError
import com.axiel7.anihyou.data.repository.BaseRepository.tryQuery
import com.axiel7.anihyou.type.MediaFormat
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaStatus
import com.axiel7.anihyou.type.MediaType
import kotlinx.coroutines.flow.flow

object SearchRepository {

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
        page: Int = 1,
        perPage: Int = 25,
    ) = flow {
        emit(PagedResult.Loading)

        val response = SearchMediaQuery(
            page = Optional.present(page),
            perPage = Optional.present(perPage),
            search = if (query.isNotBlank()) Optional.present(query) else Optional.absent(),
            type = Optional.present(mediaType),
            sort = Optional.present(sort),
            genre_in = if (genreIn.isNullOrEmpty()) Optional.absent()
            else Optional.present(genreIn),
            tag_in = if (tagIn.isNullOrEmpty()) Optional.absent()
            else Optional.present(tagIn),
            format_in = if (formatIn.isNullOrEmpty()) Optional.absent()
            else Optional.present(formatIn),
            status_in = if (statusIn.isNullOrEmpty()) Optional.absent()
            else Optional.present(statusIn),
            seasonYear = Optional.presentIfNotNull(year),
            onList = if (onList == true) Optional.present(true) else Optional.absent()
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(PagedResult.Error(message = error))
        else {
            val media = response?.data?.Page?.media?.filterNotNull()
            val pageInfo = response?.data?.Page?.pageInfo
            if (media != null) emit(
                PagedResult.Success(
                    data = media,
                    nextPage = if (pageInfo?.hasNextPage == true)
                        pageInfo.currentPage?.plus(1)
                    else null
                )
            )
            else emit(PagedResult.Error(message = "Error"))
        }
    }

    fun searchCharacter(
        query: String,
        page: Int = 1,
        perPage: Int = 25,
    ) = flow {
        emit(PagedResult.Loading)

        val response = SearchCharacterQuery(
            page = Optional.present(page),
            perPage = Optional.present(perPage),
            search = Optional.present(query)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(PagedResult.Error(message = error))
        else {
            val characters = response?.data?.Page?.characters?.filterNotNull()
            val pageInfo = response?.data?.Page?.pageInfo
            if (characters != null) emit(
                PagedResult.Success(
                    data = characters,
                    nextPage = if (pageInfo?.hasNextPage == true)
                        pageInfo.currentPage?.plus(1)
                    else null
                )
            )
            else emit(PagedResult.Error(message = "Error"))
        }
    }

    fun searchStaff(
        query: String,
        page: Int = 1,
        perPage: Int = 25,
    ) = flow {
        emit(PagedResult.Loading)

        val response = SearchStaffQuery(
            page = Optional.present(page),
            perPage = Optional.present(perPage),
            search = Optional.present(query)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(PagedResult.Error(message = error))
        else {
            val staff = response?.data?.Page?.staff?.filterNotNull()
            val pageInfo = response?.data?.Page?.pageInfo
            if (staff != null) emit(
                PagedResult.Success(
                    data = staff,
                    nextPage = if (pageInfo?.hasNextPage == true)
                        pageInfo.currentPage?.plus(1)
                    else null
                )
            )
            else emit(PagedResult.Error(message = "Error"))
        }
    }

    fun searchStudio(
        query: String,
        page: Int = 1,
        perPage: Int = 25,
    ) = flow {
        emit(PagedResult.Loading)

        val response = SearchStudioQuery(
            page = Optional.present(page),
            perPage = Optional.present(perPage),
            search = Optional.present(query)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(PagedResult.Error(message = error))
        else {
            val studios = response?.data?.Page?.studios?.filterNotNull()
            val pageInfo = response?.data?.Page?.pageInfo
            if (studios != null) emit(
                PagedResult.Success(
                    data = studios,
                    nextPage = if (pageInfo?.hasNextPage == true)
                        pageInfo.currentPage?.plus(1)
                    else null
                )
            )
            else emit(PagedResult.Error(message = "Error"))
        }
    }

    fun searchUser(
        query: String,
        page: Int = 1,
        perPage: Int = 25,
    ) = flow {
        emit(PagedResult.Loading)

        val response = SearchUserQuery(
            page = Optional.present(page),
            perPage = Optional.present(perPage),
            search = Optional.present(query)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(PagedResult.Error(message = error))
        else {
            val users = response?.data?.Page?.users?.filterNotNull()
            val pageInfo = response?.data?.Page?.pageInfo
            if (users != null) emit(
                PagedResult.Success(
                    data = users,
                    nextPage = if (pageInfo?.hasNextPage == true)
                        pageInfo.currentPage?.plus(1)
                    else null
                )
            )
            else emit(PagedResult.Error(message = "Error"))
        }
    }

    fun getGenreTagCollection() = flow {
        emit(DataResult.Loading)

        val response = GenreTagCollectionQuery().tryQuery()
        val error = response.getError()
        if (error != null) emit(DataResult.Error(message = error))
        else {
            if (response?.data != null) emit(
                DataResult.Success(
                    data = GenresAndTags(
                        genres = response.data?.GenreCollection?.filterNotNull().orEmpty(),
                        tags = response.data?.MediaTagCollection?.filterNotNull()?.map { it.name }
                            .orEmpty()
                    )
                )
            )
            else emit(DataResult.Error(message = "Error"))
        }
    }
}