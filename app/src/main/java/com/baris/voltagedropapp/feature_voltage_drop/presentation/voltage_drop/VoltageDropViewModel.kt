package com.baris.voltagedropapp.feature_voltage_drop.presentation.voltage_drop

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baris.voltagedropapp.feature_voltage_drop.domain.model.Project
import com.baris.voltagedropapp.feature_voltage_drop.domain.model.VoltageDropChart
import com.baris.voltagedropapp.feature_voltage_drop.domain.use_case.ProjectUseCases
import com.baris.voltagedropapp.feature_voltage_drop.presentation.utils.UIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoltageDropViewModel @Inject constructor(
    private val projectUseCases: ProjectUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _screenState = mutableStateOf(VoltageDropState())
    val screenState: State<VoltageDropState> = _screenState

    private val _uiEvent = MutableSharedFlow<UIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val projectId: String = savedStateHandle.get<String>("projectId") ?: ""
    private val powerChartIndex: String = savedStateHandle.get<String>("powerChartIndex") ?: ""

    init {
        viewModelScope.launch {
            val project = getProjectById(projectId)
            project?.let { p ->
                val currentPowerChart = p.powerChartList[powerChartIndex.toInt()]
                var voltageDropChartList = currentPowerChart.voltageDropChartList
                if (voltageDropChartList.size < 3) {
                    voltageDropChartList =
                        createVoltageDropList(p, powerChartIndex.toInt(), voltageDropChartList)
                }
                val totalVoltageDrop = getTotalVoltageDrop(voltageDropChartList)
                _screenState.value = _screenState.value.copy(
                    project = p,
                    powerChartState = currentPowerChart,
                    voltageDropState = totalVoltageDrop,
                    descriptionState = currentPowerChart.description,
                    voltageDropList = voltageDropChartList
                )
            }
        }
    }

    private suspend fun getProjectById(projectId: String): Project? {
        return projectUseCases.getProject(projectId)
    }

    private fun createVoltageDropList(
        project: Project,
        powerChartIndex: Int,
        voltageDropChartList: MutableList<VoltageDropChart>
    ): MutableList<VoltageDropChart> {
        val newVoltageDropCharts = List(3 - voltageDropChartList.size) { VoltageDropChart() }
        val updatedVoltageDropChartList =
            voltageDropChartList.toMutableList() // Ensure it's mutable
        updatedVoltageDropChartList.addAll(newVoltageDropCharts)
        val updatedPowerChart =
            project.powerChartList[powerChartIndex].copy(voltageDropChartList = updatedVoltageDropChartList)
        val updatedProject =
            project.copy(powerChartList = project.powerChartList.toMutableList().apply {
                set(powerChartIndex, updatedPowerChart)
            })
        viewModelScope.launch {
            projectUseCases.updateProject(updatedProject)
        }
        return updatedVoltageDropChartList
    }

    private fun getTotalVoltageDrop(voltageDropChartList: List<VoltageDropChart>): String {
        var total = 0.0
        voltageDropChartList.forEach { voltageDropChart ->
            total += voltageDropChart.voltageDrop.toDoubleOrNull() ?: 0.0
        }
        return String.format("%.3f", total)
    }
}