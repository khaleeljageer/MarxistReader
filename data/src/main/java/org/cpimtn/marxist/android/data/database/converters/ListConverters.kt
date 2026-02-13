package org.cpimtn.marxist.android.data.database.converters

import androidx.room.TypeConverter

/**
 * Room type converters for list persistence. Kept separate from entities (SRP).
 * Supports List<Int> for post tags/categories IDs.
 */
class ListConverters {

    @TypeConverter
    fun fromIntListString(value: String): List<Int> =
        if (value.isBlank()) emptyList() else value.split(",").map { it.toIntOrNull() ?: 0 }

    @TypeConverter
    fun toIntListString(list: List<Int>): String =
        list.joinToString(",")
}
