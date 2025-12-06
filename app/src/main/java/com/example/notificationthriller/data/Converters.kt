package com.example.notificationthriller.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Room type converters for complex data types
 */
class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromChoiceList(value: List<Choice>?): String? {
        if (value == null) return null
        val type = object : TypeToken<List<Choice>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toChoiceList(value: String?): List<Choice>? {
        if (value == null) return null
        val type = object : TypeToken<List<Choice>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromIntList(value: List<Int>?): String? {
        if (value == null) return null
        val type = object : TypeToken<List<Int>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toIntList(value: String?): List<Int>? {
        if (value == null) return null
        val type = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromIntMap(value: Map<Int, Int>?): String? {
        if (value == null) return null
        val type = object : TypeToken<Map<Int, Int>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toIntMap(value: String?): Map<Int, Int>? {
        if (value == null) return null
        val type = object : TypeToken<Map<Int, Int>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        if (value == null) return null
        val type = object : TypeToken<List<String>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        if (value == null) return null
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type)
    }
}
