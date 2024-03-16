package com.baris.voltagedropapp.feature_voltage_drop.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.baris.voltagedropapp.feature_voltage_drop.data.utils.Converters
import com.baris.voltagedropapp.feature_voltage_drop.data.utils.DateConverter
import com.baris.voltagedropapp.feature_voltage_drop.data.utils.UUIDConverter
import com.baris.voltagedropapp.feature_voltage_drop.domain.model.Project

@Database(entities = [Project::class], version = 6, exportSchema = false)
@TypeConverters(DateConverter::class, UUIDConverter::class, Converters::class)
abstract class ProjectDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao

    companion object {
        const val DATABASE_NAME = "projects_db"
    }
}