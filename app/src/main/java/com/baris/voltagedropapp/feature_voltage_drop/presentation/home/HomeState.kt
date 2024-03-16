package com.baris.voltagedropapp.feature_voltage_drop.presentation.home

import com.baris.voltagedropapp.feature_voltage_drop.domain.model.Project
import kotlinx.coroutines.flow.Flow

data class HomeState(
    val inputTitleValue: String = "",
    val projectList: Flow<List<Project>>? = null,
    val isAddButtonVisible: Boolean = false
)
