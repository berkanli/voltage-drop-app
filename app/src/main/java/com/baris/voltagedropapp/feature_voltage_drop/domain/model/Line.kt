package com.baris.voltagedropapp.feature_voltage_drop.domain.model

data class Line(
    val lineNumber: Int,
    val outlet: String = "",
    val circuitBreaker: String = "",
    val phaseBalance: String = "",
    val power: String = "",
    val description: String = "",
)
