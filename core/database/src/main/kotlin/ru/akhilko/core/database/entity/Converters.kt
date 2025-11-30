package ru.akhilko.core.database.entity

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.datetime.DayOfWeek
import ru.akhilko.christian_calendar.core.model.*

class Converters {

    private val gson = Gson()

    // LiturgicalInfo
    @TypeConverter
    fun fromLiturgicalInfo(value: LiturgicalInfo?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toLiturgicalInfo(value: String?): LiturgicalInfo? {
        return value?.let { gson.fromJson(it, LiturgicalInfo::class.java) }
    }

    // FastingInfo
    @TypeConverter
    fun fromFastingInfo(value: FastingInfo?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toFastingInfo(value: String?): FastingInfo? {
        return value?.let { gson.fromJson(it, FastingInfo::class.java) }
    }

    // List<Reading>
    @TypeConverter
    fun fromReadingList(value: List<Reading>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toReadingList(value: String?): List<Reading>? {
        val listType = object : TypeToken<List<Reading>>() {}.type
        return value?.let { gson.fromJson(it, listType) }
    }

    // List<SaintInfo>
    @TypeConverter
    fun fromSaintInfoList(value: List<SaintInfo>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toSaintInfoList(value: String?): List<SaintInfo>? {
        val listType = object : TypeToken<List<SaintInfo>>() {}.type
        return value?.let { gson.fromJson(it, listType) }
    }

    // DayOfWeek
    @TypeConverter
    fun fromDayOfWeek(value: DayOfWeek?): String? {
        return value?.name
    }

    @TypeConverter
    fun toDayOfWeek(value: String?): DayOfWeek? {
        return value?.let { DayOfWeek.valueOf(it) }
    }
}