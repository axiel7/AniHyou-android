package com.axiel7.anihyou.core.database.customlinks

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "custom_links")
data class CustomLinkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val uri: String,
    val spaceSeparator: Char,
    val mediaType: String,
    val titleLanguage: String?,
)
