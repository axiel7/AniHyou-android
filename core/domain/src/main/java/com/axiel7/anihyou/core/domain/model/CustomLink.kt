package com.axiel7.anihyou.core.domain.model

import android.content.Intent
import androidx.compose.runtime.Stable
import androidx.core.net.toUri
import com.axiel7.anihyou.core.base.CUSTOM_URL_NAME_PLACEHOLDER
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

    fun mediaLink(title: String) = uri.replace(
        CUSTOM_URL_NAME_PLACEHOLDER,
        title.replace(' ', spaceSeparator)
    )

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
