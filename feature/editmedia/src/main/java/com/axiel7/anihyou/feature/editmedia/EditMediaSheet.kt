package com.axiel7.anihyou.feature.editmedia

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.model.canUseAdvancedScoring
import com.axiel7.anihyou.core.model.maxValue
import com.axiel7.anihyou.core.model.media.duration
import com.axiel7.anihyou.core.model.media.icon
import com.axiel7.anihyou.core.model.media.isAnime
import com.axiel7.anihyou.core.model.media.isManga
import com.axiel7.anihyou.core.model.media.localized
import com.axiel7.anihyou.core.network.fragment.BasicMediaDetails
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.type.MediaListStatus
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.network.type.ScoreFormat
import com.axiel7.anihyou.core.ui.composables.PlainPreference
import com.axiel7.anihyou.core.ui.composables.SelectableIconToggleButton
import com.axiel7.anihyou.core.ui.composables.SwitchPreference
import com.axiel7.anihyou.core.ui.composables.common.SmallCircularProgressIndicator
import com.axiel7.anihyou.core.ui.composables.scores.RatingView
import com.axiel7.anihyou.core.ui.composables.sheet.ModalBottomSheet
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.core.common.utils.ContextUtils.showToast
import com.axiel7.anihyou.core.common.utils.DateUtils.toEpochMillis
import com.axiel7.anihyou.feature.editmedia.composables.CustomListsDialog
import com.axiel7.anihyou.feature.editmedia.composables.DeleteMediaEntryDialog
import com.axiel7.anihyou.feature.editmedia.composables.EditMediaDateField
import com.axiel7.anihyou.feature.editmedia.composables.EditMediaDatePicker
import com.axiel7.anihyou.feature.editmedia.composables.EditMediaProgressRow
import com.axiel7.anihyou.feature.editmedia.composables.ScoreView
import kotlinx.coroutines.CoroutineScope
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMediaSheet(
    mediaDetails: BasicMediaDetails,
    listEntry: BasicMediaListEntry?,
    bottomPadding: Dp = 0.dp,
    scope: CoroutineScope = rememberCoroutineScope(),
    onEntryUpdated: (updatedListEntry: BasicMediaListEntry?) -> Unit,
    onDismissed: () -> Unit,
) {
    val viewModel: EditMediaViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(mediaDetails) {
        viewModel.setMediaDetails(mediaDetails)
    }

    LaunchedEffect(listEntry) {
        viewModel.setListEntry(listEntry)
        if (listEntry == null) {
            viewModel.fillCustomLists(mediaDetails.type)
        }
    }

    EditMediaSheetContent(
        uiState = uiState,
        event = viewModel,
        bottomPadding = bottomPadding,
        scope = scope,
        onEntryUpdated = onEntryUpdated,
        onDismissed = onDismissed,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun EditMediaSheetContent(
    uiState: EditMediaUiState,
    event: EditMediaEvent?,
    bottomPadding: Dp = 0.dp,
    scope: CoroutineScope = rememberCoroutineScope(),
    sheetState: SheetState = rememberModalBottomSheetState(),
    onEntryUpdated: (updatedListEntry: BasicMediaListEntry?) -> Unit,
    onDismissed: () -> Unit,
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val datePickerState = rememberDatePickerState()
    val isKeyboardVisible = WindowInsets.isImeVisible
    val keyboardController = LocalSoftwareKeyboardController.current

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
            event?.onErrorDisplayed()
            context.showToast(uiState.error)
        }
    }

    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            onEntryUpdated(uiState.listEntry)
            event?.setUpdateSuccess(false)
            onDismissed()
        }
    }

    ModalBottomSheet(
        onDismissed = onDismissed,
        scope = scope,
        sheetState = sheetState,
        windowInsets = WindowInsets(0, 0, 0, 0),
        properties = ModalBottomSheetProperties(shouldDismissOnBackPress = false),
    ) { dismiss ->
        BackHandler(enabled = true) {
            if (isKeyboardVisible) keyboardController?.hide()
            else dismiss()
        }
        // Cancel / Save buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { dismiss() }) {
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(bottom = 32.dp + bottomPadding),
            horizontalAlignment = Alignment.Start
        ) {
            // Status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
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
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            event?.onChangeStatus(status)
                        }
                    )
                }
            }

            // Progress
            EditMediaProgressRow(
                label = if (uiState.mediaDetails?.isAnime() == true) stringResource(R.string.episodes)
                else stringResource(R.string.chapters),
                icon = if (uiState.mediaDetails?.isAnime() == true) R.drawable.play_arrow_24
                else R.drawable.book_24,
                progress = uiState.progress,
                modifier = Modifier.padding(
                    start = 0.dp,
                    end = 16.dp
                ),
                totalProgress = uiState.mediaDetails?.duration(),
                onValueChange = { event?.onChangeProgress(it.toIntOrNull()) },
                onMinusClick = { event?.onChangeProgress(uiState.progress?.minus(1)) },
                onPlusClick = { event?.onChangeProgress(uiState.progress?.plus(1) ?: 1) }
            )

            if (uiState.mediaDetails?.isManga() == true) {
                EditMediaProgressRow(
                    label = stringResource(R.string.volumes),
                    icon = R.drawable.bookmark_24,
                    progress = uiState.volumeProgress,
                    modifier = Modifier.padding(end = 16.dp, top = 8.dp),
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
                ScoreView(
                    format = uiState.scoreFormat,
                    rating = uiState.score,
                    onRatingChanged = { event?.onChangeScore(it) },
                    modifier = when (uiState.scoreFormat) {
                        ScoreFormat.POINT_10,
                        ScoreFormat.POINT_10_DECIMAL,
                        ScoreFormat.POINT_100 -> Modifier.padding(top = 8.dp, end = 16.dp)

                        else -> Modifier.padding(start = 8.dp, top = 16.dp, end = 8.dp)
                    }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // Dates
            EditMediaDateField(
                date = uiState.startedAt,
                label = stringResource(R.string.start_date),
                icon = R.drawable.calendar_today_24,
                removeDate = { event?.setStartedAt(null) },
                onClick = {
                    datePickerState.selectedDateMillis = uiState.startedAt?.toEpochMillis()
                    event?.onDateDialogOpen(dateType = 1)
                }
            )
            EditMediaDateField(
                date = uiState.completedAt,
                label = stringResource(R.string.end_date),
                icon = R.drawable.event_available_24,
                removeDate = { event?.setCompletedAt(null) },
                onClick = {
                    datePickerState.selectedDateMillis = uiState.completedAt?.toEpochMillis()
                    event?.onDateDialogOpen(dateType = 2)
                }
            )

            // Repeat
            EditMediaProgressRow(
                label = stringResource(R.string.repeat_count),
                icon = R.drawable.repeat_24,
                progress = uiState.repeatCount,
                modifier = Modifier.padding(end = 16.dp),
                totalProgress = null,
                onValueChange = { event?.onChangeRepeatCount(it.toIntOrNull()) },
                onMinusClick = { event?.onChangeRepeatCount(uiState.repeatCount?.minus(1)) },
                onPlusClick = { event?.onChangeRepeatCount(uiState.repeatCount?.plus(1) ?: 1) }
            )

            // Custom lists
            PlainPreference(
                title = stringResource(R.string.custom_lists),
                icon = R.drawable.list_alt_24,
                iconTint = LocalContentColor.current,
                iconPadding = PaddingValues(
                    start = 16.dp,
                    top = 16.dp,
                    bottom = 16.dp
                ),
                isLoading = uiState.isLoading,
                onClick = {
                    if (uiState.customLists == null) event?.getCustomLists()
                    else event?.toggleCustomListsDialog(true)
                }
            )

            SwitchPreference(
                title = stringResource(R.string.hide_from_status_lists),
                preferenceValue = uiState.isHiddenFromStatusLists,
                icon = R.drawable.visibility_off_24,
                iconTint = LocalContentColor.current,
                iconPadding = PaddingValues(
                    start = 16.dp,
                    top = 16.dp,
                    bottom = 16.dp
                ),
                onValueChange = { event?.setIsHiddenFromStatusLists(it) },
            )

            SwitchPreference(
                title = stringResource(R.string.list_private),
                preferenceValue = uiState.isPrivate,
                icon = R.drawable.lock_24,
                iconTint = LocalContentColor.current,
                iconPadding = PaddingValues(
                    start = 16.dp,
                    top = 16.dp,
                    bottom = 16.dp
                ),
                onValueChange = { event?.setIsPrivate(it) },
            )

            // Notes
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.notes_24),
                    contentDescription = stringResource(R.string.notes),
                    modifier = Modifier.padding(start = 16.dp)
                )
                OutlinedTextField(
                    value = uiState.notes.orEmpty(),
                    onValueChange = { event?.setNotes(it) },
                    placeholder = {
                        Text(text = stringResource(R.string.notes))
                    },
                    singleLine = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent
                    )
                )
            }

            if (uiState.scoreFormat.canUseAdvancedScoring() && uiState.advancedScoringEnabled) {
                uiState.advancedScoresNames.forEach { score ->
                    RatingView(
                        maxValue = uiState.scoreFormat.maxValue(),
                        modifier = Modifier
                            .padding(top = 8.dp, end = 8.dp),
                        label = score,
                        rating = uiState.advancedScores.getOrDefault(score, null),
                        showAsDecimal = true,
                        decimalLength = 6,
                        onRatingChanged = { event?.setAdvancedScore(key = score, value = it) }
                    )
                }
            }

            // Delete
            PlainPreference(
                title = stringResource(R.string.delete),
                titleTint = MaterialTheme.colorScheme.error,
                icon = R.drawable.delete_24,
                iconTint = MaterialTheme.colorScheme.error,
                iconPadding = PaddingValues(
                    start = 16.dp,
                    top = 16.dp,
                    bottom = 16.dp
                ),
                enabled = !uiState.isNewEntry,
                onClick = { event?.toggleDeleteDialog(true) }
            )
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
                sheetState = SheetState(
                    skipPartiallyExpanded = true,
                    density = LocalDensity.current,
                    initialValue = SheetValue.Expanded
                ),
                onEntryUpdated = {},
                onDismissed = {}
            )
        }
    }
}