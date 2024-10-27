package com.axiel7.anihyou.data.model.character

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.CharacterDetailsQuery
import com.axiel7.anihyou.R

@Composable
fun CharacterDetailsQuery.Character.genderLocalized() = when (gender) {
    "Male" -> stringResource(R.string.male)
    "Female" -> stringResource(R.string.female)
    "Non-binary" -> stringResource(R.string.non_binary)
    else -> gender
}