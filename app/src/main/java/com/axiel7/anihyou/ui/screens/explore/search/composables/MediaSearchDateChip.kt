package com.axiel7.anihyou.ui.screens.explore.search.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.GenericLocalizable
import com.axiel7.anihyou.data.model.media.icon
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.type.MediaSeason
import com.axiel7.anihyou.ui.composables.common.DialogWithRadioSelection
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.DateUtils

@Composable
fun MediaSearchDateChip(
    startYear: Int?,
    endYear: Int?,
    season: MediaSeason?,
    onStartYearChanged: (Int?) -> Unit,
    onEndYearChanged: (Int?) -> Unit,
    onSeasonChanged: (MediaSeason?) -> Unit,
) {
    val years = remember {
        DateUtils.seasonYears.map { GenericLocalizable(it) }.toTypedArray()
    }
    var openStartDialog by remember { mutableStateOf(false) }
    var openEndDialog by remember { mutableStateOf(false) }
    var openSeasonMenu by remember { mutableStateOf(false) }

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
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = startYear != null,
            onClick = { openStartDialog = true },
            label = {
                Text(text = startYear?.toString() ?: stringResource(R.string.from_year))
            }
        )
        Text(text = " - ")
        FilterChip(
            selected = endYear != null,
            onClick = { openEndDialog = true },
            label = {
                Text(text = endYear?.toString() ?: stringResource(R.string.to_year))
            }
        )

        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .wrapContentSize(Alignment.TopStart)
        ) {
            AssistChip(
                onClick = { openSeasonMenu = !openSeasonMenu },
                label = { Text(text = season?.localized() ?: stringResource(R.string.season)) },
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.arrow_drop_down_24),
                        contentDescription = "dropdown",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
            DropdownMenu(
                expanded = openSeasonMenu,
                onDismissRequest = { openSeasonMenu = false }
            ) {
                MediaSeason.knownEntries.forEach {
                    DropdownMenuItem(
                        text = { Text(text = it.localized()) },
                        onClick = {
                            onSeasonChanged(it.takeIf { it != season })
                            openSeasonMenu = false
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(
                                    id = if (season == it) R.drawable.check_24 else it.icon()
                                ),
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun MediaSearchYearChipPreview() {
    AniHyouTheme {
        Surface {
            MediaSearchDateChip(
                startYear = null,
                endYear = null,
                season = null,
                onStartYearChanged = {},
                onEndYearChanged = {},
                onSeasonChanged = {},
            )
        }
    }
}