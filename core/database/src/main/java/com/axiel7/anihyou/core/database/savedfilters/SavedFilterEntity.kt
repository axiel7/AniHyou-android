package com.axiel7.anihyou.core.database.savedfilters

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "saved_filters")
data class SavedFilterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val searchType: String,
    val mediaSort: String?,
    val genreIn: List<String>?,
    val genreNot: List<String>?,
    val tagIn: List<String>?,
    val tagNot: List<String>?,
    val minimumTagPercentage: Int,
    val mediaFormats: List<String>?,
    val mediaStatuses: List<String>?,
    val startYear: Int?,
    val endYear: Int?,
    val season: String?,
    val episodesChaptersRange: IntRange?,
    val durationVolumesRange: IntRange?,
    val onMyList: Boolean?,
    val isDoujin: Boolean?,
    val isAdult: Boolean?,
    val country: String?,
    val sources: List<String>?,
)
