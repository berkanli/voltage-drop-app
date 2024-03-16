package com.baris.voltagedropapp.feature_voltage_drop.domain.model

data class LoadChart(
    val tableNum: String = "",
    val lineList: MutableList<Line> = mutableListOf(),
    val installedPower: String = "",
    val demandPower: String = "",
    val biggestMomentumLine: String = ""
)
