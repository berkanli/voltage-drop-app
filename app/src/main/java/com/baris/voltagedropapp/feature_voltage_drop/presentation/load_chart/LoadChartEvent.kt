package com.baris.voltagedropapp.feature_voltage_drop.presentation.load_chart

sealed class LoadChartEvent {
    data class OutletValueEntered(val enteredValue: String) : LoadChartEvent()
    data class CircuitBreakerValueEntered(val enteredValue: String) : LoadChartEvent()
    data class PhaseBalanceValueEntered(val enteredValue: String) : LoadChartEvent()
    data class PowerValueEntered(val enteredValue: String) : LoadChartEvent()
    data class DescriptionValueEntered(val enteredValue: String) : LoadChartEvent()
    data class DeleteLineButtonClicked(val index: Int) : LoadChartEvent()
    object CreateLineButtonClicked : LoadChartEvent()
}
