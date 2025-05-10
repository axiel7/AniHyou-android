package com.axiel7.anihyou.core.ui.composables.sheet

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheet(
    onDismissed: () -> Unit,
    modifier: Modifier = Modifier,
    scope: CoroutineScope = rememberCoroutineScope(),
    sheetState: SheetState = rememberModalBottomSheetState(),
    windowInsets: WindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom),
    properties: ModalBottomSheetProperties = ModalBottomSheetDefaults.properties,
    content: @Composable (ColumnScope.(dismiss: () -> Unit) -> Unit)
) {
    fun dismiss() {
        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismissed() }
    }
    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest = { dismiss() },
        modifier = modifier,
        sheetState = sheetState,
        contentWindowInsets = { windowInsets },
        properties = properties,
    ) {
        content { dismiss() }
    }
}