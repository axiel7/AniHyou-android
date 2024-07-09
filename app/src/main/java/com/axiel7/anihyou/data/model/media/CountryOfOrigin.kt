package com.axiel7.anihyou.data.model.media

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.apollographql.apollo.api.Adapter
import com.apollographql.apollo.api.CustomScalarAdapters
import com.apollographql.apollo.api.json.JsonReader
import com.apollographql.apollo.api.json.JsonWriter
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Colorable
import com.axiel7.anihyou.data.model.base.Localizable
import com.axiel7.anihyou.ui.theme.stat_dark_40
import com.axiel7.anihyou.ui.theme.stat_dark_50
import com.axiel7.anihyou.ui.theme.stat_dark_60
import com.axiel7.anihyou.ui.theme.stat_dark_blue
import com.axiel7.anihyou.ui.theme.stat_dark_on40
import com.axiel7.anihyou.ui.theme.stat_dark_on50
import com.axiel7.anihyou.ui.theme.stat_dark_on60
import com.axiel7.anihyou.ui.theme.stat_dark_onBlue
import com.axiel7.anihyou.ui.theme.stat_light_40
import com.axiel7.anihyou.ui.theme.stat_light_50
import com.axiel7.anihyou.ui.theme.stat_light_60
import com.axiel7.anihyou.ui.theme.stat_light_blue
import com.axiel7.anihyou.ui.theme.stat_light_on40
import com.axiel7.anihyou.ui.theme.stat_light_on50
import com.axiel7.anihyou.ui.theme.stat_light_on60
import com.axiel7.anihyou.ui.theme.stat_light_onBlue

enum class CountryOfOrigin(
    val code: String
) : Localizable, Colorable {
    JAPAN("JP"),
    SOUTH_KOREA("KR"),
    CHINA("CN"),
    TAIWAN("TW");

    @Composable
    override fun primaryColor(): Color {
        val isDark = isSystemInDarkTheme()
        return when (this) {
            JAPAN -> if (isDark) stat_dark_blue else stat_light_blue
            SOUTH_KOREA -> if (isDark) stat_dark_60 else stat_light_60
            CHINA -> if (isDark) stat_dark_40 else stat_light_40
            TAIWAN -> if (isDark) stat_dark_50 else stat_light_50
        }
    }

    @Composable
    override fun onPrimaryColor(): Color {
        val isDark = isSystemInDarkTheme()
        return when (this) {
            JAPAN -> if (isDark) stat_dark_onBlue else stat_light_onBlue
            SOUTH_KOREA -> if (isDark) stat_dark_on60 else stat_light_on60
            CHINA -> if (isDark) stat_dark_on40 else stat_light_on40
            TAIWAN -> if (isDark) stat_dark_on50 else stat_light_on50
        }
    }

    @Composable
    override fun localized() = when (this) {
        JAPAN -> stringResource(R.string.japan)
        SOUTH_KOREA -> stringResource(R.string.south_korea)
        CHINA -> stringResource(R.string.china)
        TAIWAN -> stringResource(R.string.taiwan)
    }

    companion object {
        fun valueOf(code: String) = entries.find { it.code == code }

        val countryOfOriginAdapter = object : Adapter<CountryOfOrigin> {
            override fun fromJson(
                reader: JsonReader,
                customScalarAdapters: CustomScalarAdapters
            ): CountryOfOrigin {
                val value = reader.nextString()!!
                return valueOf(code = value)!!
            }

            override fun toJson(
                writer: JsonWriter,
                customScalarAdapters: CustomScalarAdapters,
                value: CountryOfOrigin
            ) {
                writer.value(value.code)
            }
        }
    }
}