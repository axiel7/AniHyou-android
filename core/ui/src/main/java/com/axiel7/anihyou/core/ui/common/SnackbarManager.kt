package com.axiel7.anihyou.core.ui.common

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.axiel7.anihyou.core.base.ANIHYOU_AUTH_URL
import com.axiel7.anihyou.core.common.utils.ContextUtils.openActionView
import com.axiel7.anihyou.core.resources.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Stable
class SnackbarManager(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private val snackbarHostState = SnackbarHostState()
    private var currentShowingSnackbar: Job? = null

    @Composable
    fun SnackbarHost() {
        SnackbarHost(snackbarHostState)
    }

    fun showNotLoggedInSnackbar(): Job {
        currentShowingSnackbar?.cancel()
        return scope.launch {
            val result = snackbarHostState.showSnackbar(
                message = context.getString(R.string.not_logged_text),
                actionLabel = context.getString(R.string.login),
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                context.openActionView(ANIHYOU_AUTH_URL)
            }
        }.also {
            currentShowingSnackbar = it
        }
    }

    fun showMessage(@StringRes stringRes: Int): Job {
        currentShowingSnackbar?.cancel()
        return scope.launch {
            snackbarHostState.showSnackbar(
                message = context.getString(stringRes),
                duration = SnackbarDuration.Short
            )
        }.also {
            currentShowingSnackbar = it
        }
    }
}

@Composable
fun rememberSnackbarManager(
    scope: CoroutineScope = rememberCoroutineScope()
): SnackbarManager {
    val context = LocalContext.current

    return remember(scope, context) {
        SnackbarManager(
            context = context,
            scope = scope
        )
    }
}
