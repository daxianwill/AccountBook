package com.sym.accountbook.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date
import java.util.HashMap

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    @TypeConverter
    fun fromLongDoubleMap(map: Map<Long, Double>?): String? {
        return map?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toLongDoubleMap(json: String?): Map<Long, Double> {
        if (json == null) return HashMap()
        val type = object : TypeToken<Map<Long, Double>>() {}.type
        return gson.fromJson(json, type) ?: HashMap()
    }

    @TypeConverter
    fun fromStringPairMap(map: Map<String, Pair<Double, Double>>?): String? {
        return map?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toStringPairMap(json: String?): Map<String, Pair<Double, Double>> {
        if (json == null) return HashMap()
        val type = object : TypeToken<Map<String, Pair<Double, Double>>>() {}.type
        return gson.fromJson(json, type) ?: HashMap()
    }
}
