package com.axiel7.anihyou.core.model.staff

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.network.StaffDetailsQuery
import com.axiel7.anihyou.core.resources.R

@Composable
fun StaffDetailsQuery.Staff.yearsActiveFormatted(): String? {
    yearsActive?.getOrNull(0)?.let { startYear ->
        val possibleEndYear = yearsActive!!.getOrNull(1)
        return if (yearsActive!!.size > 1 && possibleEndYear != null) {
            "$startYear-$possibleEndYear"
        } else {
            "$startYear-${stringResource(R.string.present)}"
        }
    }
    return null
}