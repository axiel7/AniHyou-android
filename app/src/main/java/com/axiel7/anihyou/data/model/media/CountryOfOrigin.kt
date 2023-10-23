package com.axiel7.anihyou.data.model.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.apollographql.apollo3.api.Adapter
import com.apollographql.apollo3.api.CustomScalarAdapters
import com.apollographql.apollo3.api.json.JsonReader
import com.apollographql.apollo3.api.json.JsonWriter
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Localizable

enum class CountryOfOrigin(
    val code: String
) : Localizable {
    JAPAN("JP"),
    SOUTH_KOREA("KR"),
    CHINA("CN"),
    TAIWAN("TW");

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