package ru.akhilko.core.database.util

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class StringListConverter {
    @TypeConverter
    fun fromStringList(value: List<String>): String = Json.encodeToString(value)

    @TypeConverter
    fun toStringList(value: String): List<String> = Json.decodeFromString(value)

    @TypeConverter
    fun fromStringMap(value: Map<String, List<String>>): String = Json.encodeToString(value)

    @TypeConverter
    fun toStringMap(value: String): Map<String, List<String>> = Json.decodeFromString(value)
}