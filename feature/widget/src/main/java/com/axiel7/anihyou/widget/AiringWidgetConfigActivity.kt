package com.axiel7.anihyou.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.lifecycle.lifecycleScope
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.PlainPreference
import com.axiel7.anihyou.core.ui.composables.bottomShape
import com.axiel7.anihyou.core.ui.composables.topShape
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import kotlinx.coroutines.launch

class AiringWidgetConfigActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val isColoredState = mutableStateOf(true)
        lifecycleScope.launch {
            getCurrentConfig(appWidgetId)[AiringWidget.IS_COLORED_KEY]?.let {
                isColoredState.value = it
            }
        }

        setContent {
            val isColored by remember { isColoredState }
            Content(
                isColored = isColored,
                onClickSave = { isColored ->
                    lifecycleScope.launch { saveConfig(appWidgetId, isColored) }
                }
            )
        }
    }

    private suspend fun getCurrentConfig(appWidgetId: Int): Preferences {
        val glanceId = GlanceAppWidgetManager(this).getGlanceIdBy(appWidgetId)
        return getAppWidgetState(this, PreferencesGlanceStateDefinition, glanceId).toPreferences()
    }

    private suspend fun saveConfig(appWidgetId: Int, isColored: Boolean) {
        val glanceId = GlanceAppWidgetManager(this).getGlanceIdBy(appWidgetId)
        updateAppWidgetState(this, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            prefs.toMutablePreferences().apply {
                this[AiringWidget.IS_COLORED_KEY] = isColored
            }
        }
        AiringWidget().update(this, glanceId)

        val resultValue = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        setResult(RESULT_OK, resultValue)
        finish()
    }
}


@Preview
@Composable
private fun Content(
    isColored: Boolean = true,
    onClickSave: (Boolean) -> Unit = {},
) {
    var isColored by remember { mutableStateOf(isColored) }

    AniHyouTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.widget_config_title)) }
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text(text = stringResource(R.string.save)) },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.save_24),
                            contentDescription = null,
                        )
                    },
                    onClick = { onClickSave(isColored) }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.list_style),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                PlainPreference(
                    title = stringResource(R.string.style_colored),
                    icon = R.drawable.check_24.takeIf { isColored },
                    onClick = { isColored = true },
                    shape = topShape,
                    containerColor = if (isColored) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceContainerHigh
                )

                PlainPreference(
                    title = stringResource(R.string.style_monochrome),
                    icon = R.drawable.check_24.takeIf { !isColored },
                    onClick = { isColored = false },
                    shape = bottomShape,
                    containerColor = if (!isColored) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceContainerHigh
                )
            }
        }
    }
}
