package com.axiel7.anihyou.core.model.character

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.network.type.CharacterRole
import com.axiel7.anihyou.core.resources.R

@Composable
fun CharacterRole.localized() = when (this) {
    CharacterRole.MAIN -> stringResource(R.string.role_main)
    CharacterRole.SUPPORTING -> stringResource(R.string.role_supporting)
    CharacterRole.BACKGROUND -> stringResource(R.string.role_background)
    CharacterRole.UNKNOWN__ -> stringResource(R.string.unknown)
}