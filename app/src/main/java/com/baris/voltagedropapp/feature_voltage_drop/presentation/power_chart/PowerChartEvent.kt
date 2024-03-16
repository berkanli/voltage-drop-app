package com.baris.voltagedropapp.feature_voltage_drop.presentation.power_chart

sealed class PowerChartEvent {
    data class DescriptionValueEntered(val enteredValue: String) : PowerChartEvent()
    data class PercentValueEntered(val enteredValue: String) : PowerChartEvent()
    data class AmountValueEntered(val enteredValue: String) : PowerChartEvent()
    data class DeleteButtonClicked(val index: Int) : PowerChartEvent()
    object CreateButtonClicked : PowerChartEvent()
}
