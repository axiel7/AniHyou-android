package com.axiel7.anihyou.core.model.character

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.network.CharacterDetailsQuery
import com.axiel7.anihyou.core.resources.R

@Composable
fun CharacterDetailsQuery.Character.genderLocalized() = when (gender) {
    "Male" -> stringResource(R.string.male)
    "Female" -> stringResource(R.string.female)
    "Non-binary" -> stringResource(R.string.non_binary)
    else -> gender
}