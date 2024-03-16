package com.baris.voltagedropapp.feature_voltage_drop.presentation.home

import com.baris.voltagedropapp.feature_voltage_drop.domain.model.Project

sealed class HomeEvent{
    data class ValueEntered(val enteredValue: String) : HomeEvent()
    data class AddButtonClicked(val project: Project): HomeEvent()
    data class DeleteProject(val project: Project): HomeEvent()
}
