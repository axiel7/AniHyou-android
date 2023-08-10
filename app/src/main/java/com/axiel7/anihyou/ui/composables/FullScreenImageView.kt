package com.axiel7.anihyou.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.axiel7.anihyou.R
import com.axiel7.anihyou.utils.ContextUtils.openShareSheet

const val URL_ARGUMENT = "{url}"
const val FULLSCREEN_IMAGE_DESTINATION = "full_image/$URL_ARGUMENT"

@Composable
fun FullScreenImageView(
    imageUrl: String?,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    var hasClicked by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .navigationBarsPadding()
            .clickable(enabled = !hasClicked) {
                if (!hasClicked) {
                    hasClicked = true
                    onDismiss()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "image",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalIconButton(onClick = { context.openShareSheet(imageUrl ?: "") }) {
                Icon(painter = painterResource(R.drawable.share_24), contentDescription = "close")
            }
            FilledTonalIconButton(onClick = onDismiss) {
                Icon(painter = painterResource(R.drawable.close_24), contentDescription = "close")
            }
        }
    }
}