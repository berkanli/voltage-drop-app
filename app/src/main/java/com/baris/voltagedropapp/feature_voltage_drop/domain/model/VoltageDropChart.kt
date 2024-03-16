package com.baris.voltagedropapp.feature_voltage_drop.domain.model

data class VoltageDropChart(
    val voltageDrop: String = "",
    val length: String = "",
    val watt: String = "",
    val isSingle: Boolean = true,
    val coefficient: String = "",
    val area: String = "",
    val singleOrThree: String = "",
)