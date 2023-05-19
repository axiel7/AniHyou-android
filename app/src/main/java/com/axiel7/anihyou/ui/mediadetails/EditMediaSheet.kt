package com.axiel7.anihyou.ui.mediadetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.App
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.PreferencesDataStore.SCORE_FORMAT_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.rememberPreference
import com.axiel7.anihyou.data.model.duration
import com.axiel7.anihyou.data.model.icon
import com.axiel7.anihyou.data.model.isAnime
import com.axiel7.anihyou.data.model.isManga
import com.axiel7.anihyou.data.model.localized
import com.axiel7.anihyou.data.model.maxValue
import com.axiel7.anihyou.fragment.BasicMediaDetails
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.ui.composables.ClickableOutlinedTextField
import com.axiel7.anihyou.ui.composables.SelectableIconToggleButton
import com.axiel7.anihyou.ui.composables.media.FiveStarRatingView
import com.axiel7.anihyou.ui.composables.media.SliderRatingView
import com.axiel7.anihyou.ui.composables.media.SmileyRatingView
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.ContextUtils.showToast
import com.axiel7.anihyou.utils.DateUtils.millisToLocalDate
import com.axiel7.anihyou.utils.DateUtils.toEpochMillis
import com.axiel7.anihyou.utils.DateUtils.toLocalized
import com.axiel7.anihyou.utils.StringUtils.toStringOrEmpty
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMediaSheet(
    sheetState: SheetState,
    mediaDetails: BasicMediaDetails,
    listEntry: BasicMediaListEntry?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val datePickerState = rememberDatePickerState()
    val viewModel: EditMediaViewModel = viewModel(key = "${mediaDetails.id}") {
        EditMediaViewModel(
            mediaDetails = mediaDetails,
            listEntry = listEntry
        )
    }
    val scoreFormat by rememberPreference(SCORE_FORMAT_PREFERENCE_KEY, App.scoreFormat.name)

    if (viewModel.openDatePicker) {
        EditMediaDatePicker(
            viewModel = viewModel,
            datePickerState = datePickerState,
            onDateSelected = {
                when (viewModel.selectedDateType) {
                    1 -> { viewModel.startDate = it.millisToLocalDate() }
                    2 -> { viewModel.endDate = it.millisToLocalDate() }
                }
            }
        )
    }

    if (viewModel.openDeleteDialog) {
        DeleteMediaEntryDialog(viewModel = viewModel)
    }

    LaunchedEffect(viewModel.message) {
        if (viewModel.message != null) {
            context.showToast(viewModel.message)
            viewModel.message = null
        }
    }

    LaunchedEffect(viewModel.updateSuccess) {
        if (viewModel.updateSuccess) {
            onDismiss()
            viewModel.updateSuccess = false
        }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cancel / Save buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(R.string.cancel))
                }

                if (viewModel.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }

                Button(onClick = { scope.launch { viewModel.updateListEntry() } }) {
                    Text(text = stringResource(R.string.save))
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
                MediaListStatus.knownValues().forEach { status ->
                    SelectableIconToggleButton(
                        icon = status.icon(),
                        tooltipText = status.localized(),
                        value = status,
                        selectedValue = viewModel.status,
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
                progress = viewModel.progress,
                modifier = Modifier.padding(horizontal = 16.dp),
                totalProgress = mediaDetails.duration(),
                onValueChange = { viewModel.onChangeProgress(it.toIntOrNull()) },
                onMinusClick = { viewModel.onChangeProgress(viewModel.progress?.minus(1)) },
                onPlusClick = { viewModel.onChangeProgress(viewModel.progress?.plus(1)) }
            )

            if (mediaDetails.isManga()) {
                EditMediaProgressRow(
                    label = stringResource(R.string.volumes),
                    progress = viewModel.volumeProgress,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                    totalProgress = mediaDetails.volumes,
                    onValueChange = { viewModel.onChangeVolumeProgress(it.toIntOrNull()) },
                    onMinusClick = { viewModel.onChangeVolumeProgress(viewModel.volumeProgress?.minus(1)) },
                    onPlusClick = { viewModel.onChangeVolumeProgress(viewModel.volumeProgress?.plus(1)) }
                )
            }

            // Score
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (scoreFormat) {
                    ScoreFormat.POINT_10.name,
                    ScoreFormat.POINT_10_DECIMAL.name,
                    ScoreFormat.POINT_100.name -> {
                        SliderRatingView(
                            maxValue = ScoreFormat.valueOf(scoreFormat!!).maxValue(),
                            modifier = Modifier
                                .padding(start = 8.dp, top = 16.dp, end = 8.dp),
                            initialRating = viewModel.score ?: 0.0,
                            showAsDecimal = scoreFormat == ScoreFormat.POINT_10_DECIMAL.name,
                            onRatingChanged = {
                                viewModel.score = it
                            }
                        )
                    }

                    ScoreFormat.POINT_5.name -> {
                        FiveStarRatingView(
                            modifier = Modifier.fillMaxWidth(),
                            initialRating = viewModel.score ?: 0.0,
                            onRatingChanged = {
                                viewModel.score = it
                            }
                        )
                    }

                    ScoreFormat.POINT_3.name -> {
                        SmileyRatingView(
                            modifier = Modifier.fillMaxWidth(),
                            initialRating = viewModel.score ?: 0.0,
                            onRatingChanged = {
                                viewModel.score = it
                            }
                        )
                    }

                    else -> {}
                }
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Dates
            ClickableOutlinedTextField(
                value = viewModel.startDate.toLocalized(),
                onValueChange = {  },
                label = { Text(text = stringResource(R.string.start_date)) },
                trailingIcon = {
                    if (viewModel.startDate != null) {
                        IconButton(onClick = { viewModel.startDate = null }) {
                            Icon(
                                painter = painterResource(R.drawable.cancel_24),
                                contentDescription = stringResource(R.string.delete)
                            )
                        }
                    }
                },
                onClick = {
                    datePickerState.setSelection(viewModel.startDate?.toEpochMillis())
                    viewModel.selectedDateType = 1
                    viewModel.openDatePicker = true
                }
            )
            ClickableOutlinedTextField(
                value = viewModel.endDate.toLocalized(),
                onValueChange = {  },
                modifier = Modifier.padding(vertical = 8.dp),
                label = { Text(text = stringResource(R.string.end_date)) },
                trailingIcon = {
                    if (viewModel.endDate != null) {
                        IconButton(onClick = { viewModel.endDate = null }) {
                            Icon(
                                painter = painterResource(R.drawable.cancel_24),
                                contentDescription = stringResource(R.string.delete)
                            )
                        }
                    }
                },
                onClick = {
                    datePickerState.setSelection(viewModel.endDate?.toEpochMillis())
                    viewModel.selectedDateType = 2
                    viewModel.openDatePicker = true
                }
            )

            // Repeat
            EditMediaProgressRow(
                label = stringResource(R.string.repeat_count),
                progress = viewModel.repeatCount,
                modifier = Modifier.padding(16.dp),
                totalProgress = null,
                onValueChange = { viewModel.onChangeRepeatCount(it.toIntOrNull()) },
                onMinusClick = { viewModel.onChangeRepeatCount(viewModel.repeatCount?.minus(1)) },
                onPlusClick = { viewModel.onChangeRepeatCount(viewModel.repeatCount?.plus(1)) }
            )

            // Delete
            Button(
                onClick = { viewModel.openDeleteDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                enabled = !viewModel.isNewEntry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(text = stringResource(R.string.delete))
            }
        }//:Column
    }//:Sheet
}

@Composable
fun DeleteMediaEntryDialog(viewModel: EditMediaViewModel) {
    val scope = rememberCoroutineScope()
    AlertDialog(
        onDismissRequest = { viewModel.openDeleteDialog = false },
        confirmButton = {
            TextButton(
                onClick = {
                    scope.launch { viewModel.deleteListEntry() }
                    viewModel.openDeleteDialog = false
                }
            ) {
                Text(text = stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.openDeleteDialog = false }) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        title = { Text(text = stringResource(R.string.delete)) },
        text = { Text(text = stringResource(R.string.delete_confirmation)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMediaDatePicker(
    viewModel: EditMediaViewModel,
    datePickerState: DatePickerState,
    onDateSelected: (Long) -> Unit
) {
    val dateConfirmEnabled by remember {
        derivedStateOf { datePickerState.selectedDateMillis != null }
    }

    DatePickerDialog(
        onDismissRequest = { viewModel.openDatePicker = false },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.openDatePicker = false
                    onDateSelected(datePickerState.selectedDateMillis!!)
                },
                enabled = dateConfirmEnabled
            ) {
                Text(text = stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.openDatePicker = false }) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun EditMediaProgressRow(
    label: String,
    progress: Int?,
    modifier: Modifier,
    totalProgress: Int?,
    onValueChange: (String) -> Unit,
    onMinusClick: () -> Unit,
    onPlusClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onMinusClick,
            modifier = Modifier.weight(1f)
        ) {
            Text(text = "-1")
        }
        OutlinedTextField(
            value = progress.toStringOrEmpty(),
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(2f)
                .padding(horizontal = 16.dp),
            label = { Text(text = label) },
            suffix = {
                totalProgress?.let { Text(text = "/$it") }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedButton(
            onClick = onPlusClick,
            modifier = Modifier.weight(1f)
        ) {
            Text(text = "+1")
        }
    }
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