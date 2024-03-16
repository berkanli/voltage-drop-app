package com.baris.voltagedropapp.feature_voltage_drop.domain.model

data class PowerChart(
    val description: String = "",
    val loadChart: LoadChart = LoadChart(),
    val amount: String = "",
    val percent: String = "",
    val colonPower: String = "",
    val voltageDropChartList: MutableList<VoltageDropChart> = mutableListOf(),
)