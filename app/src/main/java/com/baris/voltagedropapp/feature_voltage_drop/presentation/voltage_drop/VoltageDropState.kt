package com.baris.voltagedropapp.feature_voltage_drop.presentation.voltage_drop

import com.baris.voltagedropapp.feature_voltage_drop.domain.model.PowerChart
import com.baris.voltagedropapp.feature_voltage_drop.domain.model.Project
import com.baris.voltagedropapp.feature_voltage_drop.domain.model.VoltageDropChart

data class VoltageDropState(
    val descriptionState: String = "",
    val project: Project = Project(),
    val powerChartState: PowerChart = PowerChart(),
    val voltageDropState: String = "0.0",
    val voltageDropList: List<VoltageDropChart> = emptyList()
)
