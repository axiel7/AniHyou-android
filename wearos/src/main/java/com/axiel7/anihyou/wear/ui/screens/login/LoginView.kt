package com.axiel7.anihyou.wear.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.wear.ui.composables.ScrollableColumn
import com.axiel7.anihyou.wear.ui.theme.AniHyouTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginView(
    modifier: Modifier = Modifier
) {
    val viewModel: LoginViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoginContent(
        uiState = uiState,
        event = viewModel,
        modifier = modifier,
    )
}

@Composable
private fun LoginContent(
    uiState: LoginUiState,
    event: LoginEvent?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    Scaffold(
        positionIndicator = {
            PositionIndicator(scrollState = scrollState)
        }
    ) {
        ScrollableColumn(
            scrollState = scrollState,
            modifier = modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = uiState.error ?: stringResource(R.string.login_required_in_phone),
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center
            )

            Button(
                onClick = { event?.launchLoginIntent(context) }
            ) {
                Text(text = stringResource(R.string.login))
            }
        }
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
private fun LoginPreview() {
    AniHyouTheme {
        LoginContent(
            uiState = LoginUiState(),
            event = null,
        )
    }
}