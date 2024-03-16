package com.baris.voltagedropapp.feature_voltage_drop.presentation.power_chart

import com.baris.voltagedropapp.feature_voltage_drop.domain.model.PowerChart

data class PowerChartState(
    val inputDescription: String = "",
    val inputPercent: String = "",
    val inputAmount: String = "",
    val mainColumnPowerState: String = "",
    val powerChartList: List<PowerChart> = emptyList(),
)
