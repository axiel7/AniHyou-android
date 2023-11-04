package com.axiel7.anihyou.ui.screens.mediadetails.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.maxValue
import com.axiel7.anihyou.data.model.media.duration
import com.axiel7.anihyou.data.model.media.icon
import com.axiel7.anihyou.data.model.media.isAnime
import com.axiel7.anihyou.data.model.media.isManga
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.fragment.BasicMediaDetails
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.ui.composables.ClickableOutlinedTextField
import com.axiel7.anihyou.ui.composables.SelectableIconToggleButton
import com.axiel7.anihyou.ui.composables.common.SmallCircularProgressIndicator
import com.axiel7.anihyou.ui.composables.common.TextCheckbox
import com.axiel7.anihyou.ui.composables.scores.FiveStarRatingView
import com.axiel7.anihyou.ui.composables.scores.SliderRatingView
import com.axiel7.anihyou.ui.composables.scores.SmileyRatingView
import com.axiel7.anihyou.ui.screens.mediadetails.edit.composables.CustomListsDialog
import com.axiel7.anihyou.ui.screens.mediadetails.edit.composables.DeleteMediaEntryDialog
import com.axiel7.anihyou.ui.screens.mediadetails.edit.composables.EditMediaDatePicker
import com.axiel7.anihyou.ui.screens.mediadetails.edit.composables.EditMediaProgressRow
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.ContextUtils.showToast
import com.axiel7.anihyou.utils.DateUtils.toEpochMillis
import com.axiel7.anihyou.utils.DateUtils.toLocalized

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMediaSheet(
    sheetState: SheetState,
    mediaDetails: BasicMediaDetails,
    listEntry: BasicMediaListEntry?,
    bottomPadding: Dp = 0.dp,
    onDismiss: (updatedListEntry: BasicMediaListEntry?) -> Unit
) {
    val context = LocalContext.current
    val datePickerState = rememberDatePickerState()

    val viewModel: EditMediaViewModel = hiltViewModel()

    LaunchedEffect(mediaDetails) {
        viewModel.setMediaDetails(mediaDetails)
    }

    LaunchedEffect(listEntry) {
        viewModel.setListEntry(listEntry)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scoreFormat by viewModel.scoreFormat.collectAsStateWithLifecycle()

    if (uiState.openDatePicker) {
        EditMediaDatePicker(
            datePickerState = datePickerState,
            onDateSelected = {
                when (uiState.selectedDateType) {
                    1 -> viewModel.setStartedAt(it)
                    2 -> viewModel.setCompletedAt(it)
                }
            },
            onDismiss = { viewModel.onDateDialogClosed() }
        )
    }

    if (uiState.openDeleteDialog) {
        DeleteMediaEntryDialog(
            onClickOk = {
                viewModel.deleteListEntry()
            },
            onDismiss = {
                viewModel.toggleDeleteDialog(false)
            }
        )
    }

    if (uiState.openCustomListsDialog && uiState.customLists != null) {
        CustomListsDialog(
            lists = uiState.customLists!!,
            isLoading = uiState.isLoading,
            onConfirm = {
                viewModel.updateCustomLists(it)
            },
            onDismiss = { viewModel.toggleCustomListsDialog(false) }
        )
    }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            context.showToast(uiState.error)
            viewModel.onErrorDisplayed()
        }
    }

    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            onDismiss(uiState.listEntry)
            viewModel.setUpdateSuccess(false)
        }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { onDismiss(uiState.listEntry) },
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(bottom = 32.dp + bottomPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cancel / Save buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { onDismiss(listEntry) }) {
                    Text(text = stringResource(R.string.cancel))
                }

                Button(onClick = { viewModel.updateListEntry() }) {
                    if (uiState.isLoading) {
                        SmallCircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(text = stringResource(R.string.save))
                    }
                }
            }

            // Status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MediaListStatus.knownEntries.forEach { status ->
                    SelectableIconToggleButton(
                        icon = status.icon(),
                        tooltipText = status.localized(
                            mediaType = mediaDetails.type ?: MediaType.UNKNOWN__
                        ),
                        value = status,
                        selectedValue = uiState.status,
                        onClick = {
                            viewModel.onChangeStatus(status)
                        }
                    )
                }
            }

            // Progress
            EditMediaProgressRow(
                label = if (mediaDetails.isAnime()) stringResource(R.string.episodes)
                else stringResource(R.string.chapters),
                progress = uiState.progress,
                modifier = Modifier.padding(horizontal = 16.dp),
                totalProgress = mediaDetails.duration(),
                onValueChange = { viewModel.onChangeProgress(it.toIntOrNull()) },
                onMinusClick = { viewModel.onChangeProgress(uiState.progress?.minus(1)) },
                onPlusClick = { viewModel.onChangeProgress(uiState.progress?.plus(1)) }
            )

            if (mediaDetails.isManga()) {
                EditMediaProgressRow(
                    label = stringResource(R.string.volumes),
                    progress = uiState.volumeProgress,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                    totalProgress = mediaDetails.volumes,
                    onValueChange = { viewModel.onChangeVolumeProgress(it.toIntOrNull()) },
                    onMinusClick = {
                        viewModel.onChangeVolumeProgress(
                            uiState.volumeProgress?.minus(1)
                        )
                    },
                    onPlusClick = {
                        viewModel.onChangeVolumeProgress(
                            uiState.volumeProgress?.plus(1)
                        )
                    }
                )
            }

            // Score
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (scoreFormat) {
                    ScoreFormat.POINT_10,
                    ScoreFormat.POINT_10_DECIMAL,
                    ScoreFormat.POINT_100 -> {
                        SliderRatingView(
                            maxValue = scoreFormat?.maxValue() ?: 0.0,
                            modifier = Modifier
                                .padding(start = 8.dp, top = 16.dp, end = 8.dp),
                            initialRating = uiState.score ?: 0.0,
                            showAsDecimal = scoreFormat == ScoreFormat.POINT_10_DECIMAL,
                            onRatingChanged = viewModel::onChangeScore
                        )
                    }

                    ScoreFormat.POINT_5 -> {
                        FiveStarRatingView(
                            modifier = Modifier.padding(start = 8.dp, top = 16.dp, end = 8.dp),
                            initialRating = uiState.score ?: 0.0,
                            onRatingChanged = viewModel::onChangeScore
                        )
                    }

                    ScoreFormat.POINT_3 -> {
                        SmileyRatingView(
                            modifier = Modifier.padding(start = 8.dp, top = 16.dp, end = 8.dp),
                            rating = uiState.score ?: 0.0,
                            onRatingChanged = viewModel::onChangeScore
                        )
                    }

                    else -> {}
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // Dates
            ClickableOutlinedTextField(
                value = uiState.startedAt.toLocalized(),
                onValueChange = { },
                label = { Text(text = stringResource(R.string.start_date)) },
                trailingIcon = {
                    if (uiState.startedAt != null) {
                        IconButton(onClick = { viewModel.setStartedAt(null) }) {
                            Icon(
                                painter = painterResource(R.drawable.cancel_24),
                                contentDescription = stringResource(R.string.delete)
                            )
                        }
                    }
                },
                onClick = {
                    datePickerState.selectedDateMillis = uiState.startedAt?.toEpochMillis()
                    viewModel.onDateDialogOpen(dateType = 1)
                }
            )
            ClickableOutlinedTextField(
                value = uiState.completedAt.toLocalized(),
                onValueChange = { },
                modifier = Modifier.padding(vertical = 8.dp),
                label = { Text(text = stringResource(R.string.end_date)) },
                trailingIcon = {
                    if (uiState.completedAt != null) {
                        IconButton(onClick = { viewModel.setCompletedAt(null) }) {
                            Icon(
                                painter = painterResource(R.drawable.cancel_24),
                                contentDescription = stringResource(R.string.delete)
                            )
                        }
                    }
                },
                onClick = {
                    datePickerState.selectedDateMillis = uiState.completedAt?.toEpochMillis()
                    viewModel.onDateDialogOpen(dateType = 2)
                }
            )

            // Repeat
            EditMediaProgressRow(
                label = stringResource(R.string.repeat_count),
                progress = uiState.repeatCount,
                modifier = Modifier.padding(16.dp),
                totalProgress = null,
                onValueChange = { viewModel.onChangeRepeatCount(it.toIntOrNull()) },
                onMinusClick = { viewModel.onChangeRepeatCount(uiState.repeatCount?.minus(1)) },
                onPlusClick = { viewModel.onChangeRepeatCount(uiState.repeatCount?.plus(1)) }
            )

            // Custom lists
            TextButton(
                onClick = {
                    if (uiState.customLists == null) viewModel.getCustomLists()
                    else viewModel.toggleCustomListsDialog(true)
                }
            ) {
                if (uiState.isLoading) {
                    SmallCircularProgressIndicator()
                } else {
                    Icon(
                        painter = painterResource(R.drawable.list_alt_24),
                        contentDescription = stringResource(R.string.custom_lists)
                    )
                }
                Text(
                    text = stringResource(R.string.custom_lists),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            
            TextCheckbox(
                text = stringResource(R.string.list_private),
                checked = uiState.isPrivate ?: false,
                onCheckedChange = viewModel::setIsPrivate,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Notes
            OutlinedTextField(
                value = uiState.notes ?: "",
                onValueChange = viewModel::setNotes,
                modifier = Modifier.padding(horizontal = 16.dp),
                label = { Text(text = stringResource(R.string.notes)) },
                singleLine = false,
                minLines = 3
            )

            // Delete
            Button(
                onClick = { viewModel.toggleDeleteDialog(true) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                enabled = !uiState.isNewEntry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.12f),
                    disabledContentColor = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.38f)
                )
            ) {
                Text(text = stringResource(R.string.delete))
            }
        }//:Column
    }//:Sheet
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun EditMediaSheetPreview() {
    AniHyouTheme {
        Surface {
            EditMediaSheet(
                sheetState = rememberModalBottomSheetState(),
                mediaDetails = BasicMediaDetails(
                    __typename = "",
                    id = 1,
                    title = BasicMediaDetails.Title(userPreferred = ""),
                    episodes = 12,
                    chapters = null,
                    volumes = null,
                    type = MediaType.ANIME
                ),
                listEntry = null,
                onDismiss = {}
            )
        }
    }
}