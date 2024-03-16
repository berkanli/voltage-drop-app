package com.baris.voltagedropapp.feature_voltage_drop.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.Date
import java.util.UUID

@Entity(tableName = "project_table")
data class Project(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),

    @ColumnInfo(name = "project_title")
    val title: String = "",

    @ColumnInfo(name = "project_description")
    val description: String = "",

    @ColumnInfo(name = "project_entry_date")
    val entryDate: Date = Date.from(Instant.now()),

    @ColumnInfo(name = "project_power_chart")
    val powerChartList: MutableList<PowerChart> = mutableListOf(),

    @ColumnInfo(name = "project_main_colon_power")
    val mainColonPower: String = "",
)

class InvalidNoteException(message: String): Exception(message)