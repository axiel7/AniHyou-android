package com.axiel7.anihyou.core.ui.utils

import android.content.Context
import coil3.annotation.ExperimentalCoilApi
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap

object ImageUtils {

    @OptIn(ExperimentalCoilApi::class)
    suspend fun Context.getBitmapFromUrl(url: String?) = imageLoader.execute(
        ImageRequest.Builder(this)
            .data(url)
            .allowHardware(false)
            .build()
    ).image?.toBitmap()
}