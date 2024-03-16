package com.baris.voltagedropapp.feature_voltage_drop.presentation.calculator

data class CalculatorState(
    val inputPower : String = "",
    val inputVoltageDrop: String = "",
    val inputLength: String = "",
    val inputCoefficient: String = "",
    val inputArea: String = "",
    val isSingle: Boolean = true
)