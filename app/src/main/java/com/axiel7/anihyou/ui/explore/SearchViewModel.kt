package com.axiel7.anihyou.ui.explore

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.GenreTagCollectionQuery
import com.axiel7.anihyou.SearchCharacterQuery
import com.axiel7.anihyou.SearchMediaQuery
import com.axiel7.anihyou.SearchStaffQuery
import com.axiel7.anihyou.SearchUserQuery
import com.axiel7.anihyou.data.model.SearchType
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.base.BaseViewModel

class SearchViewModel : BaseViewModel() {

    private val perPage = 25

    var searchType by mutableStateOf(SearchType.ANIME)
    var mediaSort by mutableStateOf(MediaSort.SEARCH_MATCH)
    val genreCollection = mutableStateMapOf<String, Boolean>()
    val tagCollection = mutableStateMapOf<String, Boolean>()

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

    val searchedMedia = mutableStateListOf<SearchMediaQuery.Medium>()

    suspend fun searchMedia(mediaType: MediaType, query: String) {
        isLoading = true
        val selectedGenres = genreCollection.filterValues { it }.keys.toList()
        val selectedTags = tagCollection.filterValues { it }.keys.toList()
        if (selectedGenres.isNotEmpty() || selectedTags.isNotEmpty()) {
            if (mediaSort == MediaSort.SEARCH_MATCH) mediaSort = MediaSort.POPULARITY_DESC
        }

        val response = SearchMediaQuery(
            page = Optional.present(1),
            perPage = Optional.present(perPage),
            search = if (query.isNotBlank()) Optional.present(query) else Optional.absent(),
            type = Optional.present(mediaType),
            sort = Optional.present(listOf(mediaSort)),
            genre_in = if (selectedGenres.isEmpty()) Optional.absent()
            else Optional.present(selectedGenres),
            tag_in = if (selectedTags.isEmpty()) Optional.absent()
            else Optional.present(selectedTags),
        ).tryQuery()

        searchedMedia.clear()
        response?.data?.Page?.media?.filterNotNull()?.let { searchedMedia.addAll(it) }
        isLoading = false
    }

    val searchedCharacters = mutableStateListOf<SearchCharacterQuery.Character>()

    suspend fun searchCharacter(query: String) {
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

    suspend fun searchStaff(query: String) {
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

    suspend fun searchStudio(query: String) {

    }

    val searchedUsers = mutableStateListOf<SearchUserQuery.User>()

    suspend fun searchUser(query: String) {
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
        isLoadingGenres = true
        val response = GenreTagCollectionQuery().tryQuery()

        response?.data?.GenreCollection?.filterNotNull()?.forEach {
            genreCollection[it] = false
        }
        val externalGenre = if (genreCollection.size == 1) genreCollection.firstNotNullOf { it.key }
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