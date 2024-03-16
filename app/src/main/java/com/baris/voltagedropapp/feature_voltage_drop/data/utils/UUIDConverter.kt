package com.baris.voltagedropapp.feature_voltage_drop.data.utils

import androidx.room.TypeConverter
import java.util.UUID

class UUIDConverter {
    @TypeConverter
    fun fromUUID(uuid: UUID): String {
        return uuid.toString()
    }

    @TypeConverter
    fun uuidFromString(str: String?): UUID? {
        return UUID.fromString(str)
    }
}