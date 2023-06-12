package com.axiel7.anihyou.ui.explore.search

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.GenreTagCollectionQuery
import com.axiel7.anihyou.SearchCharacterQuery
import com.axiel7.anihyou.SearchMediaQuery
import com.axiel7.anihyou.SearchStaffQuery
import com.axiel7.anihyou.SearchStudioQuery
import com.axiel7.anihyou.SearchUserQuery
import com.axiel7.anihyou.data.model.SearchType
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel : BaseViewModel() {

    private val perPage = 25

    var searchType by mutableStateOf(SearchType.ANIME)
    var mediaSort by mutableStateOf(MediaSort.SEARCH_MATCH)
    val genreCollection = mutableStateMapOf<String, Boolean>()
    val tagCollection = mutableStateMapOf<String, Boolean>()
    val selectedGenres by derivedStateOf { genreCollection.filter { it.value } }
    val selectedTags by derivedStateOf { tagCollection.filter { it.value } }

    suspend fun runSearch(query: String) {
        viewModelScope.launch {
            when (searchType) {
                SearchType.ANIME -> searchMedia(MediaType.ANIME, query, clear = true)
                SearchType.MANGA -> searchMedia(MediaType.MANGA, query, clear = true)
                SearchType.CHARACTER -> searchCharacter(query)
                SearchType.STAFF -> searchStaff(query)
                SearchType.STUDIO -> searchStudio(query)
                SearchType.USER -> searchUser(query)
            }
        }
    }

    private var pageMedia = 1
    private var hasNextPageMedia = true
    val searchedMedia = mutableStateListOf<SearchMediaQuery.Medium>()

    suspend fun searchMedia(mediaType: MediaType, query: String, clear: Boolean) {
        if (clear) pageMedia = 1
        isLoading = pageMedia == 1

        val selectedGenres = genreCollection.filterValues { it }.keys.toList()
        val selectedTags = tagCollection.filterValues { it }.keys.toList()
        if (selectedGenres.isNotEmpty() || selectedTags.isNotEmpty()) {
            if (mediaSort == MediaSort.SEARCH_MATCH) mediaSort = MediaSort.POPULARITY_DESC
        }

        val response = SearchMediaQuery(
            page = Optional.present(pageMedia),
            perPage = Optional.present(perPage),
            search = if (query.isNotBlank()) Optional.present(query) else Optional.absent(),
            type = Optional.present(mediaType),
            sort = Optional.present(listOf(mediaSort)),
            genre_in = if (selectedGenres.isEmpty()) Optional.absent()
            else Optional.present(selectedGenres),
            tag_in = if (selectedTags.isEmpty()) Optional.absent()
            else Optional.present(selectedTags),
        ).tryQuery()

        if (clear) searchedMedia.clear()
        response?.data?.Page?.media?.filterNotNull()?.let { searchedMedia.addAll(it) }
        hasNextPageMedia = response?.data?.Page?.pageInfo?.hasNextPage ?: false
        pageMedia = response?.data?.Page?.pageInfo?.currentPage?.plus(1) ?: pageMedia
        isLoading = false
    }

    val searchedCharacters = mutableStateListOf<SearchCharacterQuery.Character>()

    private suspend fun searchCharacter(query: String) {
        isLoading = true
        val response = SearchCharacterQuery(
            page = Optional.present(1),
            perPage = Optional.present(perPage),
            search = Optional.present(query)
        ).tryQuery()

        searchedCharacters.clear()
        response?.data?.Page?.characters?.filterNotNull()?.let { searchedCharacters.addAll(it) }
        isLoading = false
    }

    val searchedStaff = mutableStateListOf<SearchStaffQuery.Staff>()

    private suspend fun searchStaff(query: String) {
        isLoading = true
        val response = SearchStaffQuery(
            page = Optional.present(1),
            perPage = Optional.present(perPage),
            search = Optional.present(query)
        ).tryQuery()

        searchedStaff.clear()
        response?.data?.Page?.staff?.filterNotNull()?.let { searchedStaff.addAll(it) }
        isLoading = false
    }

    val searchedStudios = mutableStateListOf<SearchStudioQuery.Studio>()

    private suspend fun searchStudio(query: String) {
        isLoading = true
        val response = SearchStudioQuery(
            page = Optional.present(1),
            perPage = Optional.present(perPage),
            search = Optional.present(query)
        ).tryQuery()

        searchedStudios.clear()
        response?.data?.Page?.studios?.filterNotNull()?.let { searchedStudios.addAll(it) }
        isLoading = false
    }

    val searchedUsers = mutableStateListOf<SearchUserQuery.User>()

    private suspend fun searchUser(query: String) {
        isLoading = true

        val response = SearchUserQuery(
            page = Optional.present(1),
            perPage = Optional.present(perPage),
            search = Optional.present(query)
        ).tryQuery()

        searchedUsers.clear()
        response?.data?.Page?.users?.filterNotNull()?.let { searchedUsers.addAll(it) }
        isLoading = false
    }

    var isLoadingGenres by mutableStateOf(false)

    suspend fun getGenreTagCollection() {
        viewModelScope.launch {
            isLoadingGenres = true
            val response = GenreTagCollectionQuery().tryQuery()

            response?.data?.GenreCollection?.filterNotNull()?.forEach {
                genreCollection[it] = false
            }
            val externalGenre =
                if (genreCollection.size == 1) genreCollection.firstNotNullOf { it.key }
                else null
            externalGenre?.let { genreCollection[externalGenre] = true }

            response?.data?.MediaTagCollection?.filterNotNull()?.forEach { tag ->
                tagCollection[tag.name] = false
            }
            val externalTag = if (tagCollection.size == 1) tagCollection.firstNotNullOf { it.key }
            else null
            externalTag?.let { tagCollection[externalTag] = true }

            isLoadingGenres = false
        }
    }

    fun unselectAllGenresAndTags() {
        viewModelScope.launch(Dispatchers.IO) {
            genreCollection.forEach { (t, _) -> genreCollection[t] = false }
            tagCollection.forEach { (t, _) -> tagCollection[t] = false }
        }
    }
}