package com.axiel7.anihyou.ui.screens.activitydetails

import com.axiel7.anihyou.data.model.activity.GenericActivity
import com.axiel7.anihyou.ui.common.state.UiState

data class ActivityDetailsUiState(
    override val isLoading: Boolean = true,
    override val error: String? = null,
    val details: GenericActivity? = null,
) : UiState() {
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setError(value: String?) = copy(error = value)
}
