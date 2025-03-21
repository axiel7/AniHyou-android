package com.axiel7.anihyou.feature.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.common.ANIHYOU_AUTH_URL
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.core.ui.utils.ContextUtils.openActionView

@Composable
fun LoginView(
    modifier: Modifier = Modifier,
    showSettingsButton: Boolean = false,
    navigateToSettings: () -> Unit = {},
) {
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.not_logged_text),
            modifier = Modifier.padding(8.dp)
        )

        Button(
            onClick = { context.openActionView(ANIHYOU_AUTH_URL) }
        ) {
            Text(text = stringResource(R.string.login))
        }
        if (showSettingsButton) {
            TextButton(onClick = navigateToSettings) {
                Icon(
                    painter = painterResource(R.drawable.settings_24),
                    contentDescription = stringResource(R.string.settings),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(text = stringResource(R.string.settings))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginViewPreview() {
    AniHyouTheme {
        Surface {
            LoginView()
        }
    }
}