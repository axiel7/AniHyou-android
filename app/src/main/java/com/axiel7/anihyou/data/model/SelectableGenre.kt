package com.axiel7.anihyou.data.model

data class SelectableGenre(
    val name: String,
    val isSelected: Boolean,
) {
    override fun equals(other: Any?): Boolean {
        return if (other is SelectableGenre) name == other.name else false
    }
    override fun hashCode(): Int {
        return name.hashCode()
    }
}
