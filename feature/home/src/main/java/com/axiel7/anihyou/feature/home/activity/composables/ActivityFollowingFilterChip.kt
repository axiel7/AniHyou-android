package com.axiel7.anihyou.feature.home.activity.composables

import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.model.base.Localizable
import com.axiel7.anihyou.core.network.FollowingsQuery
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.common.DialogWithCheckboxSelection

@Composable
fun ActivityFollowingFilterChip(
    followingUsers: List<FollowingsQuery.Following>?,
    selectedIds: List<Int>,
    onValueChanged: (List<Int>) -> Unit,
    enabled: Boolean = true,
) {
    var openDialog by remember { mutableStateOf(false) }

    if (openDialog && !followingUsers.isNullOrEmpty()) {
        val selectableUsers = remember(followingUsers) {
            followingUsers.map { FollowingLocalizable(it) }
        }
        val currentSelection = remember(selectedIds, followingUsers) {
            followingUsers.filter { it.userFollow.id in selectedIds }.map { FollowingLocalizable(it) }
        }

        DialogWithCheckboxSelection(
            values = selectableUsers,
            defaultValues = currentSelection,
            title = stringResource(R.string.users),
            onConfirm = { selected ->
                onValueChanged(selected.map { it.following.userFollow.id })
                openDialog = false
            },
            onDismiss = { openDialog = false }
        )
    }

    AssistChip(
        onClick = { openDialog = true },
        label = {
            Text(text = stringResource(R.string.activity_filter_following))
        },
        enabled = !followingUsers.isNullOrEmpty() && enabled
    )
}

@JvmInline
private value class FollowingLocalizable(
    val following: FollowingsQuery.Following
): Localizable {
    @Composable
    override fun localized(): String {
        return following.userFollow.name
    }
}