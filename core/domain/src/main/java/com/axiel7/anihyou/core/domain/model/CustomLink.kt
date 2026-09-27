package com.axiel7.anihyou.core.domain.model

import android.content.Intent
import androidx.compose.runtime.Stable
import androidx.core.net.toUri
import com.axiel7.anihyou.core.base.CUSTOM_URL_NAME_PLACEHOLDER
import com.axiel7.anihyou.core.database.customlinks.CustomLinkEntity
import com.axiel7.anihyou.core.network.type.MediaType

@Stable
data class CustomLink(
    val id: Int,
    val name: String,
    val uri: String,
    val spaceSeparator: Char,
    val mediaType: MediaType,
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
    )

    companion object {
        fun fromEntity(entity: CustomLinkEntity) = CustomLink(
            id = entity.id,
            name = entity.name,
            uri = entity.uri,
            spaceSeparator = entity.spaceSeparator,
            mediaType = MediaType.valueOf(entity.mediaType),
        )

        // to migrate from preferences
        fun fromString(string: String, mediaType: MediaType): CustomLink {
            val urlString = string.substring(1)
            val uri = urlString.toUri()
            val scheme = uri.scheme
                ?.takeIf { !it.startsWith("http") }
                ?.plus("://")
            val intent = runCatching { Intent.parseUri(urlString, 0) }
                .takeIf { uri.host == null }
                ?.getOrNull()

            val name = if (uri.host != null) scheme.orEmpty() + uri.host
            else if (intent != null) intent.action ?: urlString
            else urlString

            return CustomLink(
                id = 0,
                name = name,
                uri = urlString,
                spaceSeparator = string.first(),
                mediaType = mediaType,
            )
        }
    }
}
