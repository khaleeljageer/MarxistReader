package org.cpimtn.marxist.android.data.database.converters

import androidx.room.TypeConverter

/**
 * Room type converters for list persistence. Kept separate from entities (SRP).
 */
class ListConverters {

    @TypeConverter
    fun fromString(value: String): List<String> =
        if (value.isEmpty()) emptyList() else value.split(",")

    @TypeConverter
    fun fromList(list: List<String>): String =
        list.joinToString(",")
}
