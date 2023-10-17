package com.axiel7.anihyou.ui.screens.explore.search.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.GenericLocalizable
import com.axiel7.anihyou.ui.composables.DialogWithRadioSelection
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.DateUtils

@Composable
fun MediaSearchYearChip(
    startYear: Int?,
    endYear: Int?,
    onStartYearChanged: (Int?) -> Unit,
    onEndYearChanged: (Int?) -> Unit,
) {
    val years = remember {
        DateUtils.seasonYears.map { GenericLocalizable(it) }.toTypedArray()
    }
    var openStartDialog by remember { mutableStateOf(false) }
    var openEndDialog by remember { mutableStateOf(false) }

    if (openStartDialog || openEndDialog) {
        val year = if (openStartDialog) startYear else endYear
        DialogWithRadioSelection(
            values = years,
            defaultValue = year?.let { GenericLocalizable(year) },
            title = stringResource(
                if (openStartDialog) R.string.from_year
                else R.string.to_year
            ),
            isDeselectable = true,
            onConfirm = {
                if (openStartDialog) {
                    onStartYearChanged(it?.value)
                    openStartDialog = false
                } else if (openEndDialog) {
                    onEndYearChanged(it?.value)
                    openEndDialog = false
                }
            },
            onDismiss = {
                openStartDialog = false
                openEndDialog = false
            }
        )
    }

    Row(
        modifier = Modifier.padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AssistChip(
            onClick = { openStartDialog = true },
            label = {
                Text(text = startYear?.toString() ?: stringResource(R.string.from_year))
            }
        )
        Text(text = " - ")
        AssistChip(
            onClick = { openEndDialog = true },
            label = {
                Text(text = endYear?.toString() ?: stringResource(R.string.to_year))
            }
        )
    }
}

@Preview
@Composable
fun MediaSearchYearChipPreview() {
    AniHyouTheme {
        Surface {
            MediaSearchYearChip(
                startYear = null,
                endYear = null,
                onStartYearChanged = {},
                onEndYearChanged = {}
            )
        }
    }
}