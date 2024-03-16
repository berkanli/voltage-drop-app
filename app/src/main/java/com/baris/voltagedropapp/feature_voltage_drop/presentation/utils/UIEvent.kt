package com.baris.voltagedropapp.feature_voltage_drop.presentation.utils

sealed class UIEvent {
    data class ShowMessage(val message: String) : UIEvent()
}
