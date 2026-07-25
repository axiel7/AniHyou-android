package com.axiel7.anihyou.core.base.event

interface UiEvent {
    fun showError(error: String)
    fun onErrorDisplayed()
}