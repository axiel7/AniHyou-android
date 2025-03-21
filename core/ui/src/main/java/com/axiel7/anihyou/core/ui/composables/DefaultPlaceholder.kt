package com.axiel7.anihyou.core.ui.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import io.github.fornewid.placeholder.foundation.PlaceholderHighlight
import io.github.fornewid.placeholder.material3.fade
import io.github.fornewid.placeholder.material3.placeholder

fun Modifier.defaultPlaceholder(
    visible: Boolean
) = composed {
    this.placeholder(
        visible = visible,
        color = MaterialTheme.colorScheme.outline,
        highlight = PlaceholderHighlight.fade()
    )
}