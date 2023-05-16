package com.axiel7.anihyou.ui.explore

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.SearchMediaQuery
import com.axiel7.anihyou.data.model.SearchType
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.base.BaseViewModel

class SearchViewModel : BaseViewModel() {

    private val perPage = 25

    var searchType by mutableStateOf(SearchType.ANIME)
    var mediaSort by mutableStateOf(MediaSort.SEARCH_MATCH)

    suspend fun runSearch(query: String) {
        when (searchType) {
            SearchType.ANIME -> searchMedia(MediaType.ANIME, query)
            SearchType.MANGA -> searchMedia(MediaType.MANGA, query)
            SearchType.CHARACTER -> searchCharacter(query)
            SearchType.STAFF -> searchStaff(query)
            SearchType.STUDIO -> searchStudio(query)
            SearchType.USER -> searchUser(query)
        }
    }

    var searchedMedia = mutableStateListOf<SearchMediaQuery.Medium>()

    suspend fun searchMedia(mediaType: MediaType, query: String) {
        isLoading = true
        val response = SearchMediaQuery(
            page = Optional.present(1),
            perPage = Optional.present(perPage),
            search = if (query.isNotBlank()) Optional.present(query) else Optional.absent(),
            type = Optional.present(mediaType),
            sort = Optional.present(listOf(mediaSort)),
            genre_in = Optional.absent(),// TODO: search by genre
            tag_in = Optional.absent(),// TODO: search by tags
        ).tryQuery()

        searchedMedia.clear()
        response?.data?.Page?.media?.filterNotNull()?.let { searchedMedia.addAll(it) }
        isLoading = false
    }

    suspend fun searchCharacter(query: String) {

    }

    suspend fun searchStaff(query: String) {

    }

    suspend fun searchStudio(query: String) {

    }

    suspend fun searchUser(query: String) {

    }
}