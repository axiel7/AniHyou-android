package com.axiel7.anihyou.data.repository

import android.content.Context
import androidx.glance.appwidget.updateAll
import com.axiel7.anihyou.ui.widget.medialist.MediaListWidget
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun updateMediaListWidget() {
        MediaListWidget().updateAll(context)
    }
}