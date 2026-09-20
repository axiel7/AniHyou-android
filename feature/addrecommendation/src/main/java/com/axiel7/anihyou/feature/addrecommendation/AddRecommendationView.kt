package com.axiel7.anihyou.feature.addrecommendation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.result.LocalResultEventBus
import com.axiel7.anihyou.core.network.fragment.MediaRecommended
import com.axiel7.anihyou.core.network.type.MediaFormat
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.LocalBlurAdult
import com.axiel7.anihyou.core.ui.common.LocalNavActionManager
import com.axiel7.anihyou.core.ui.common.navigation.Route
import com.axiel7.anihyou.core.ui.common.rememberSnackbarManager
import com.axiel7.anihyou.core.ui.composables.DefaultScaffoldWithMediumTopAppBar
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.composables.common.ErrorDialogHandler
import com.axiel7.anihyou.core.ui.composables.media.MediaItemHorizontal
import com.axiel7.anihyou.core.ui.composables.media.MediaItemHorizontalPlaceholder
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.feature.addrecommendation.search.SimpleMediaSearchSheetView
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AddRecommendationView(
    arguments: Route.AddRecommendation,
) {
    val viewModel: AddRecommendationViewModel = koinViewModel(parameters = { parametersOf(arguments) })
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AddRecommendationContent(
        uiState = uiState,
        event = viewModel,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddRecommendationContent(
    uiState: AddRecommendationUiState,
    event: AddRecommendationEvent?,
) {
    val blurAdult = LocalBlurAdult.current
    val resultBus = LocalResultEventBus.current
    val navActionManager = LocalNavActionManager.current
    val scope = rememberCoroutineScope()
    val snackbarManager = rememberSnackbarManager()
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    )

    ErrorDialogHandler(uiState, onDismiss = { event?.onErrorDisplayed() })

    LaunchedEffect(uiState.isSaved) {
        if (uiState.returnedRecommendation != null && uiState.isSaved) {
            resultBus.sendResult<MediaRecommended>(uiState.returnedRecommendation)
            navActionManager.goBack()
        }
    }

    if (sheetState.isVisible) {
        SimpleMediaSearchSheetView(
            mediaType = uiState.media?.basicMediaDetails?.type ?: MediaType.ANIME,
            onSelected = { event?.insertRecommendation(it) },
            sheetState = sheetState,
        )
    }

    DefaultScaffoldWithMediumTopAppBar(
        title = stringResource(R.string.recommendations),
        snackbarHost = snackbarManager::SnackbarHost,
        navigationIcon = {
            BackIconButton(onClick = navActionManager::goBack)
        },
        scrollBehavior = topAppBarScrollBehavior,
        floatingActionButton = {
            if (uiState.recommendation != null) {
                ExtendedFloatingActionButton(
                    onClick = {
                        val mediaId = uiState.media?.id
                        val recommendationId = uiState.recommendation.id
                        if (mediaId != null) {
                            event?.saveRecommendation(mediaId, recommendationId)
                        }
                    },
                    text = {
                        Text(stringResource(R.string.save))
                    },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.save_24),
                            contentDescription = stringResource(R.string.save)
                        )
                    }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val media = uiState.media
            val rec = uiState.recommendation

            // main media
            if (media != null) {
                MediaItemHorizontal(
                    title = media.basicMediaDetails.title?.userPreferred.orEmpty(),
                    imageUrl = media.coverImage?.large,
                    blurImage = blurAdult && media.basicMediaDetails.isAdult == true,
                    score = media.averageScore ?: 0,
                    format = media.format ?: MediaFormat.UNKNOWN__,
                    year = media.startDate?.fuzzyDate?.year,
                    mediaStatus = media.status,
                    episodes = media.basicMediaDetails.episodes,
                    chapters = media.basicMediaDetails.chapters,
                    duration = media.duration,
                    genres = media.genres?.filterNotNull()?.toImmutableList(),
                    onClick = {},
                )
            } else {
                MediaItemHorizontalPlaceholder()
            }

            Spacer(modifier = Modifier.height(20.dp))

            // indicator
            Icon(
                painter = painterResource(R.drawable.arrow_downward_48),
                contentDescription = null,
                modifier = Modifier
                    .background(color = Color.Transparent)
                    .padding(8.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Spacer(modifier = Modifier.height(20.dp))

            // recommended media
            if (rec == null) {
                OutlinedCard(
                    onClick = { scope.launch { sheetState.show() } },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(140.dp),
                    border = BorderStroke(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.add_24),
                                contentDescription = stringResource(R.string.add),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(R.string.add),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            } else {
                MediaItemHorizontal(
                    title = rec.basicMediaDetails.title?.userPreferred.orEmpty(),
                    imageUrl = rec.coverImage?.large,
                    blurImage = blurAdult && rec.basicMediaDetails.isAdult == true,
                    score = rec.averageScore ?: 0,
                    format = rec.format ?: MediaFormat.UNKNOWN__,
                    year = rec.startDate?.year,
                    mediaStatus = rec.status,
                    episodes = rec.episodes,
                    chapters = rec.chapters,
                    duration = rec.duration,
                    genres = rec.genres?.filterNotNull()?.toImmutableList(),
                    onClick = { scope.launch { sheetState.show() } },
                )
            }
        }
    }
}

@Preview
@Composable
private fun AddRecommendationPreview() {
    AniHyouTheme {
        Surface {
            AddRecommendationContent(
                uiState = AddRecommendationUiState(mediaId = 0),
                event = null,
            )
        }
    }
}

