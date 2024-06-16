package com.axiel7.anihyou.ui.screens.usermedialist.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.axiel7.anihyou.R
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.ui.screens.mediadetails.edit.composables.ScoreView
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun SetScoreDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double?) -> Unit,
    scoreFormat: ScoreFormat,
    modifier: Modifier = Modifier
) {
    var score by remember { mutableStateOf<Double?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(score) }) {
                Text(text = stringResource(R.string.ok))
            }
        },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        title = {
            Text(text = stringResource(R.string.score))
        },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ScoreView(
                    format = scoreFormat,
                    rating = score,
                    onRatingChanged = { score = it },
                )
            }
        }
    )
}

@Preview
@Composable
private fun SetScoreDialogPreview() {
    AniHyouTheme {
        SetScoreDialog(
            onDismiss = {},
            onConfirm = {},
            scoreFormat = ScoreFormat.POINT_100
        )
    }
}