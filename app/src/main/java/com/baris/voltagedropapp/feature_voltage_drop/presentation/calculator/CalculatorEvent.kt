package com.baris.voltagedropapp.feature_voltage_drop.presentation.calculator

sealed class CalculatorEvent{
    object CalculateAreaClicked:CalculatorEvent()
    object CalculateVoltageDropClicked:CalculatorEvent()
    data class PhaseToggleClicked(val isSingle: Boolean): CalculatorEvent()
    data class VoltageDropValueEntered(val value: String): CalculatorEvent()
    data class LengthValueEntered(val value: String): CalculatorEvent()
    data class CoefficientValueEntered(val value: String): CalculatorEvent()
    data class AreaValueEntered(val value: String): CalculatorEvent()
}
