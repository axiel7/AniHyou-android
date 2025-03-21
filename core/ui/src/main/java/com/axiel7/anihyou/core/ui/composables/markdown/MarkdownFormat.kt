package com.axiel7.anihyou.core.ui.composables.markdown

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.model.base.Localizable
import com.axiel7.anihyou.core.resources.R

enum class MarkdownFormat(
    val syntax: String,
    val selectionOffset: Int
) : Localizable {
    BOLD("____", 2),
    ITALIC("__", 1),
    STRIKETHROUGH("~~~~", 2),
    SPOILER("~!!~", 2),
    LINK("[link](%s)", 0),
    IMAGE("img(%s)", 0),
    YOUTUBE("youtube(%s)", 0),
    VIDEO("webm(%s)", 0),
    ORDERED_LIST("\n1.", 0),
    UNORDERED_LIST("\n-", 0),
    HEADING("\n# ", 0),
    CENTERED("~~~~~~", 3),
    QUOTE("\n> ", 0),
    CODE("``", 1);

    @Composable
    override fun localized() = when (this) {
        BOLD -> stringResource(R.string.format_bold)
        ITALIC -> stringResource(R.string.format_italic)
        STRIKETHROUGH -> stringResource(R.string.format_strikethrough)
        SPOILER -> stringResource(R.string.format_spoiler)
        LINK -> stringResource(R.string.format_link)
        IMAGE -> stringResource(R.string.format_image)
        YOUTUBE -> stringResource(R.string.format_youtube)
        VIDEO -> stringResource(R.string.format_video)
        ORDERED_LIST -> stringResource(R.string.format_ol)
        UNORDERED_LIST -> stringResource(R.string.format_ul)
        HEADING -> stringResource(R.string.format_heading)
        CENTERED -> stringResource(R.string.format_centered)
        QUOTE -> stringResource(R.string.format_quote)
        CODE -> stringResource(R.string.format_code)
    }

    val icon
        get() = when (this) {
            BOLD -> R.drawable.format_bold_24
            ITALIC -> R.drawable.format_italic_24
            STRIKETHROUGH -> R.drawable.format_strikethrough_24
            SPOILER -> R.drawable.visibility_off_24
            LINK -> R.drawable.link_24
            IMAGE -> R.drawable.image_24
            YOUTUBE -> R.drawable.youtube_activity_24
            VIDEO -> R.drawable.videocam_24
            ORDERED_LIST -> R.drawable.format_list_numbered_24
            UNORDERED_LIST -> R.drawable.format_list_bulleted_24
            HEADING -> R.drawable.title_24
            CENTERED -> R.drawable.format_align_center_24
            QUOTE -> R.drawable.format_quote_24
            CODE -> R.drawable.code_24
        }
}