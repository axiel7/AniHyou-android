package com.axiel7.anihyou.core.domain.model

import android.content.Intent
import androidx.compose.runtime.Stable
import androidx.core.net.toUri
import com.axiel7.anihyou.core.base.CUSTOM_URL_ENGLISH_NAME_PLACEHOLDER
import com.axiel7.anihyou.core.base.CUSTOM_URL_ID_PLACEHOLDER
import com.axiel7.anihyou.core.base.CUSTOM_URL_NAME_PLACEHOLDER
import com.axiel7.anihyou.core.base.CUSTOM_URL_NATIVE_NAME_PLACEHOLDER
import com.axiel7.anihyou.core.base.CUSTOM_URL_ROMAJI_NAME_PLACEHOLDER
import com.axiel7.anihyou.core.database.customlinks.CustomLinkEntity
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.network.type.UserTitleLanguage

@Stable
data class CustomLink(
    val id: Int,
    val name: String,
    val uri: String,
    val spaceSeparator: Char,
    val mediaType: MediaType,
    val titleLanguage: UserTitleLanguage?,
) {
    val isIntent = uri.startsWith("intent:")

    fun mediaLink(
        title: String? = null,
        romaji: String? = null,
        english: String? = null,
        native: String? = null,
        mediaId: Int? = null,
    ): String {
        val formattedTitle = title?.replace(' ', spaceSeparator).orEmpty()
        val formattedRomaji = romaji?.replace(' ', spaceSeparator).orEmpty()
        val formattedEnglish = english?.replace(' ', spaceSeparator).orEmpty()
        val formattedNative = native?.replace(' ', spaceSeparator).orEmpty()
        val idString = mediaId?.toString() ?: "0"

        return uri
            .replace(CUSTOM_URL_NAME_PLACEHOLDER, formattedTitle)
            .replace(CUSTOM_URL_NATIVE_NAME_PLACEHOLDER, formattedNative)
            .replace(CUSTOM_URL_ROMAJI_NAME_PLACEHOLDER, formattedRomaji)
            .replace(CUSTOM_URL_ENGLISH_NAME_PLACEHOLDER, formattedEnglish)
            .replace(CUSTOM_URL_ID_PLACEHOLDER, idString)
    }

    fun mediaLink(title: String) = mediaLink(title, mediaId = 0)

    fun toEntity() = CustomLinkEntity(
        id = id,
        name = name,
        uri = uri,
        spaceSeparator = spaceSeparator,
        mediaType = mediaType.name,
        titleLanguage = titleLanguage?.name,
    )

    companion object {
        fun fromEntity(entity: CustomLinkEntity) = CustomLink(
            id = entity.id,
            name = entity.name,
            uri = entity.uri,
            spaceSeparator = entity.spaceSeparator,
            mediaType = MediaType.valueOf(entity.mediaType),
            titleLanguage = entity.titleLanguage?.let(UserTitleLanguage::valueOf),
        )

        fun extractNameFromUrl(url: String): String {
            val uri = url.toUri()
            val scheme = uri.scheme
                ?.takeIf { !it.startsWith("http") }
                ?.plus("://")
            val intent = runCatching { Intent.parseUri(url, 0) }
                .takeIf { uri.host == null }
                ?.getOrNull()

            return if (uri.host != null) scheme.orEmpty() + uri.host
            else if (intent != null) intent.action ?: url
            else url
        }

        // to migrate from preferences
        fun fromString(string: String, mediaType: MediaType): CustomLink {
            val urlString = string.substring(1)
            val name = extractNameFromUrl(urlString)

            return CustomLink(
                id = 0,
                name = name,
                uri = urlString,
                spaceSeparator = string.first(),
                mediaType = mediaType,
                titleLanguage = null,
            )
        }
    }
}
