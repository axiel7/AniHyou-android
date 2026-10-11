package com.axiel7.anihyou.core.database

import androidx.room3.ColumnTypeConverter

object DatabaseConverters {

    @ColumnTypeConverter
    fun listToString(list: List<String>): String {
        return list.joinToString(",")
    }

    @ColumnTypeConverter
    fun stringToList(string: String): List<String> {
        return string.split(",")
    }

    @ColumnTypeConverter
    fun intRangeToString(range: IntRange): String {
        return "${range.first}-${range.last}"
    }

    @ColumnTypeConverter
    fun stringToIntRange(string: String): IntRange {
        val (start, end) = string.split("-")
        return start.toInt()..end.toInt()
    }
}