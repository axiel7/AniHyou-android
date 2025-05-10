package com.axiel7.anihyou.core.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.navigation.Routes.FullScreenImage
import com.axiel7.anihyou.core.common.utils.ContextUtils.openShareSheet

@Composable
fun FullScreenImageView(
    arguments: FullScreenImage,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        SubcomposeAsyncImage(
            model = arguments.imageUrl,
            contentDescription = "image",
            modifier = Modifier.fillMaxWidth(),
            loading = {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp)
                    )
                }
            },
            error = {
                Icon(painter = painterResource(R.drawable.cancel_24), contentDescription = null)
            },
            contentScale = ContentScale.FillWidth
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalIconButton(onClick = { context.openShareSheet(arguments.imageUrl.orEmpty()) }) {
                Icon(
                    painter = painterResource(R.drawable.share_24),
                    contentDescription = stringResource(R.string.share)
                )
            }
            FilledTonalIconButton(onClick = onDismiss) {
                Icon(
                    painter = painterResource(R.drawable.close_24),
                    contentDescription = stringResource(R.string.close)
                )
            }
        }
    }
}