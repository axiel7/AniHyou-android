package com.axiel7.anihyou.widget

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.unit.ColorProvider

@Composable
fun glanceStringResource(@StringRes id: Int, vararg args: Any): String {
    return LocalContext.current.getString(id, *args)
}

fun GlanceModifier.widgetCornerRadius(): GlanceModifier {
    val cornerRadiusModifier =
        if (android.os.Build.VERSION.SDK_INT >= 31) {
            GlanceModifier.cornerRadius(android.R.dimen.system_app_widget_background_radius)
        } else {
            GlanceModifier
        }

    return this.then(cornerRadiusModifier)
}

@Composable
fun RoundedDrawableBox(
    @DrawableRes shapeRes: Int,
    color: ColorProvider,
    modifier: GlanceModifier = GlanceModifier,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.background(
            imageProvider = ImageProvider(shapeRes),
            colorFilter = ColorFilter.tint(color)
        ),
        contentAlignment = contentAlignment,
        content = content,
    )
}