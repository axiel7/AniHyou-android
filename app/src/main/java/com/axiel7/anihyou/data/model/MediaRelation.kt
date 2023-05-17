package com.axiel7.anihyou.data.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.type.MediaRelation

@Composable
fun MediaRelation.localized() = when (this) {
    MediaRelation.ADAPTATION -> stringResource(R.string.relation_adaptation)
    MediaRelation.PREQUEL -> stringResource(R.string.prequel)
    MediaRelation.SEQUEL -> stringResource(R.string.sequel)
    MediaRelation.PARENT -> stringResource(R.string.relation_parent)
    MediaRelation.SIDE_STORY -> stringResource(R.string.side_story)
    MediaRelation.CHARACTER -> stringResource(R.string.relation_character)
    MediaRelation.SUMMARY -> stringResource(R.string.relation_summary)
    MediaRelation.ALTERNATIVE -> stringResource(R.string.relation_alternative)
    MediaRelation.SPIN_OFF -> stringResource(R.string.spin_off)
    MediaRelation.OTHER -> stringResource(R.string.relation_other)
    MediaRelation.SOURCE -> stringResource(R.string.relation_source)
    MediaRelation.COMPILATION -> stringResource(R.string.relation_compilation)
    MediaRelation.CONTAINS -> stringResource(R.string.relation_contains)
    MediaRelation.UNKNOWN__ -> stringResource(R.string.unknown)
}