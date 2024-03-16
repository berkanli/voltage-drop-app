package com.baris.voltagedropapp.feature_voltage_drop.presentation.load_chart

import com.baris.voltagedropapp.feature_voltage_drop.domain.model.Line

data class LoadChartState(
    val inputLineNumber: String = "",
    val inputOutlet: String = "",
    val inputCircuitBreaker: String = "",
    val inputPhaseBalance: String = "",
    val inputPower: String = "",
    val inputDescription: String = "",
    val lineList: List<Line> = emptyList(),
    val lineListSize: Int = 0,
    val biggestPower: String = ""
)
