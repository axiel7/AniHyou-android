package com.axiel7.anihyou.core.ui.composables.character

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.axiel7.anihyou.core.network.fragment.CommonVoiceActor
import com.axiel7.anihyou.core.ui.composables.person.PersonItemHorizontal
import com.axiel7.anihyou.core.ui.composables.sheet.ModalBottomSheet
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterVoiceActorsSheet(
    voiceActors: List<CommonVoiceActor>,
    scope: CoroutineScope,
    navigateToStaffDetails: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val bottomBarPadding = WindowInsets.navigationBars.asPaddingValues()
    ModalBottomSheet(
        onDismissed = onDismiss,
        scope = scope,
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        LazyColumn(
            contentPadding = bottomBarPadding
        ) {
            items(
                items = voiceActors,
                contentType = { it }
            ) {
                PersonItemHorizontal(
                    title = it.name?.userPreferred.orEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    imageUrl = it.image?.medium,
                    subtitle = it.languageV2,
                    onClick = {
                        navigateToStaffDetails(it.id)
                    }
                )
            }
        }
    }
}