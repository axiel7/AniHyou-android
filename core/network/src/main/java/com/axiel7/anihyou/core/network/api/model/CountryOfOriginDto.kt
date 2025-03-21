package com.axiel7.anihyou.core.network.api.model

import com.apollographql.apollo.api.Adapter
import com.apollographql.apollo.api.CustomScalarAdapters
import com.apollographql.apollo.api.json.JsonReader
import com.apollographql.apollo.api.json.JsonWriter

enum class CountryOfOriginDto(
    val code: String
) {
    JAPAN("JP"),
    SOUTH_KOREA("KR"),
    CHINA("CN"),
    TAIWAN("TW");

    companion object {
        fun valueOf(code: String) = entries.find { it.code == code }

        val countryOfOriginAdapter = object : Adapter<CountryOfOriginDto> {
            override fun fromJson(
                reader: JsonReader,
                customScalarAdapters: CustomScalarAdapters
            ): CountryOfOriginDto {
                val value = reader.nextString()!!
                return valueOf(code = value)!!
            }

            override fun toJson(
                writer: JsonWriter,
                customScalarAdapters: CustomScalarAdapters,
                value: CountryOfOriginDto
            ) {
                writer.value(value.code)
            }
        }
    }
}