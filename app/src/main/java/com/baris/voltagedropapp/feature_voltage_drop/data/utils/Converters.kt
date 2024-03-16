package com.baris.voltagedropapp.feature_voltage_drop.data.utils

import androidx.room.TypeConverter
import com.baris.voltagedropapp.feature_voltage_drop.domain.model.PowerChart
import com.baris.voltagedropapp.feature_voltage_drop.domain.model.VoltageDropChart
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromJson(json: String): List<VoltageDropChart> {
        val listType = object : TypeToken<List<VoltageDropChart>>() {}.type
        return Gson().fromJson(json, listType)
    }

    @TypeConverter
    fun toJson(list: List<VoltageDropChart>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromJsonToPowerChart(json: String): List<PowerChart> {
        // Check if the JSON string represents an object instead of an array
        if (!json.startsWith("[")) {
            // If it's an object, wrap it in an array
            val jsonArray = "[$json]"
            val listType = object : TypeToken<List<PowerChart>>() {}.type
            return Gson().fromJson(jsonArray, listType)
        }
        // Otherwise, proceed with normal deserialization
        val listType = object : TypeToken<List<PowerChart>>() {}.type
        return Gson().fromJson(json, listType)
    }

    @TypeConverter
    fun fromPowerChartToJson(list: List<PowerChart>): String {
        return Gson().toJson(list)
    }
}