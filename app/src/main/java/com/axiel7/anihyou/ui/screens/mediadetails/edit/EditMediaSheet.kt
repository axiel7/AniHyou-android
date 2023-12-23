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
    val viewModel: EditMediaViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(mediaDetails) {
        viewModel.setMediaDetails(mediaDetails)
    }

    LaunchedEffect(listEntry) {
        viewModel.setListEntry(listEntry)
    }

    EditMediaSheetContent(
        uiState = uiState,
        event = viewModel,
        sheetState = sheetState,
        bottomPadding = bottomPadding,
        onDismiss = onDismiss,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditMediaSheetContent(
    uiState: EditMediaUiState,
    event: EditMediaEvent?,
    sheetState: SheetState,
    bottomPadding: Dp = 0.dp,
    onDismiss: (updatedListEntry: BasicMediaListEntry?) -> Unit
) {
    val context = LocalContext.current
    val datePickerState = rememberDatePickerState()

    if (uiState.openDatePicker) {
        EditMediaDatePicker(
            datePickerState = datePickerState,
            onDateSelected = {
                when (uiState.selectedDateType) {
                    1 -> event?.setStartedAt(it)
                    2 -> event?.setCompletedAt(it)
                }
            },
            onDismiss = { event?.onDateDialogClosed() }
        )
    }

    if (uiState.openDeleteDialog) {
        DeleteMediaEntryDialog(
            onClickOk = {
                event?.deleteListEntry()
            },
            onDismiss = {
                event?.toggleDeleteDialog(false)
            }
        )
    }

    if (uiState.openCustomListsDialog) {
        CustomListsDialog(
            lists = uiState.customLists.orEmpty(),
            isLoading = uiState.isLoading,
            onConfirm = {
                event?.updateCustomLists(it)
            },
            onDismiss = { event?.toggleCustomListsDialog(false) }
        )
    }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            context.showToast(uiState.error)
            event?.onErrorDisplayed()
        }
    }

    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            onDismiss(uiState.listEntry)
            event?.setUpdateSuccess(false)
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
                TextButton(onClick = { onDismiss(uiState.listEntry) }) {
                    Text(text = stringResource(R.string.cancel))
                }

                Button(onClick = { event?.updateListEntry() }) {
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
                            mediaType = uiState.mediaDetails?.type ?: MediaType.UNKNOWN__
                        ),
                        value = status,
                        selectedValue = uiState.status,
                        onClick = {
                            event?.onChangeStatus(status)
                        }
                    )
                }
            }

            // Progress
            EditMediaProgressRow(
                label = if (uiState.mediaDetails?.isAnime() == true) stringResource(R.string.episodes)
                else stringResource(R.string.chapters),
                progress = uiState.progress,
                modifier = Modifier.padding(horizontal = 16.dp),
                totalProgress = uiState.mediaDetails?.duration(),
                onValueChange = { event?.onChangeProgress(it.toIntOrNull()) },
                onMinusClick = { event?.onChangeProgress(uiState.progress?.minus(1)) },
                onPlusClick = { event?.onChangeProgress(uiState.progress?.plus(1) ?: 1) }
            )

            if (uiState.mediaDetails?.isManga() == true) {
                EditMediaProgressRow(
                    label = stringResource(R.string.volumes),
                    progress = uiState.volumeProgress,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                    totalProgress = uiState.mediaDetails.volumes,
                    onValueChange = { event?.onChangeVolumeProgress(it.toIntOrNull()) },
                    onMinusClick = {
                        event?.onChangeVolumeProgress(
                            uiState.volumeProgress?.minus(1)
                        )
                    },
                    onPlusClick = {
                        event?.onChangeVolumeProgress(
                            uiState.volumeProgress?.plus(1) ?: 1
                        )
                    }
                )
            }

            // Score
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (uiState.scoreFormat) {
                    ScoreFormat.POINT_10,
                    ScoreFormat.POINT_10_DECIMAL,
                    ScoreFormat.POINT_100 -> {
                        SliderRatingView(
                            maxValue = uiState.scoreFormat.maxValue(),
                            modifier = Modifier
                                .padding(start = 8.dp, top = 16.dp, end = 8.dp),
                            initialRating = uiState.score ?: 0.0,
                            showAsDecimal = uiState.scoreFormat == ScoreFormat.POINT_10_DECIMAL,
                            onRatingChanged = { event?.onChangeScore(it) }
                        )
                    }

                    ScoreFormat.POINT_5 -> {
                        FiveStarRatingView(
                            modifier = Modifier.padding(start = 8.dp, top = 16.dp, end = 8.dp),
                            initialRating = uiState.score ?: 0.0,
                            onRatingChanged = { event?.onChangeScore(it) }
                        )
                    }

                    ScoreFormat.POINT_3 -> {
                        SmileyRatingView(
                            modifier = Modifier.padding(start = 8.dp, top = 16.dp, end = 8.dp),
                            rating = uiState.score ?: 0.0,
                            onRatingChanged = { event?.onChangeScore(it) }
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
                        IconButton(onClick = { event?.setStartedAt(null) }) {
                            Icon(
                                painter = painterResource(R.drawable.cancel_24),
                                contentDescription = stringResource(R.string.delete)
                            )
                        }
                    }
                },
                onClick = {
                    datePickerState.selectedDateMillis = uiState.startedAt?.toEpochMillis()
                    event?.onDateDialogOpen(dateType = 1)
                }
            )
            ClickableOutlinedTextField(
                value = uiState.completedAt.toLocalized(),
                onValueChange = { },
                modifier = Modifier.padding(vertical = 8.dp),
                label = { Text(text = stringResource(R.string.end_date)) },
                trailingIcon = {
                    if (uiState.completedAt != null) {
                        IconButton(onClick = { event?.setCompletedAt(null) }) {
                            Icon(
                                painter = painterResource(R.drawable.cancel_24),
                                contentDescription = stringResource(R.string.delete)
                            )
                        }
                    }
                },
                onClick = {
                    datePickerState.selectedDateMillis = uiState.completedAt?.toEpochMillis()
                    event?.onDateDialogOpen(dateType = 2)
                }
            )

            // Repeat
            EditMediaProgressRow(
                label = stringResource(R.string.repeat_count),
                progress = uiState.repeatCount,
                modifier = Modifier.padding(16.dp),
                totalProgress = null,
                onValueChange = { event?.onChangeRepeatCount(it.toIntOrNull()) },
                onMinusClick = { event?.onChangeRepeatCount(uiState.repeatCount?.minus(1)) },
                onPlusClick = { event?.onChangeRepeatCount(uiState.repeatCount?.plus(1) ?: 1) }
            )

            // Custom lists
            TextButton(
                onClick = {
                    if (uiState.customLists == null) event?.getCustomLists()
                    else event?.toggleCustomListsDialog(true)
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
                onCheckedChange = { event?.setIsPrivate(it) },
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Notes
            OutlinedTextField(
                value = uiState.notes.orEmpty(),
                onValueChange = { event?.setNotes(it) },
                modifier = Modifier.padding(horizontal = 16.dp),
                label = { Text(text = stringResource(R.string.notes)) },
                singleLine = false,
                minLines = 3
            )

            // Delete
            Button(
                onClick = { event?.toggleDeleteDialog(true) },
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
            EditMediaSheetContent(
                uiState = EditMediaUiState(),
                event = null,
                sheetState = rememberModalBottomSheetState(),
                onDismiss = {}
            )
        }
    }
}