package com.baris.voltagedropapp.feature_voltage_drop.presentation.calculator

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
class CalculatorViewModel @Inject constructor(
    private val projectUseCases: ProjectUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _screenState = mutableStateOf(CalculatorState())
    val screenState: State<CalculatorState> = _screenState

    private val _uiEventFlow = MutableSharedFlow<UIEvent>()
    val uiEventFlow = _uiEventFlow.asSharedFlow()

    private val projectId: String = savedStateHandle.get<String>("projectId") ?: ""
    private val powerChartIndex: String = savedStateHandle.get<String>("powerChartIndex") ?: ""
    private val index: String = savedStateHandle.get<String>("index") ?: ""

    init {
        viewModelScope.launch {
            val power = getPower(index)
            val project = getProjectById(projectId)
            project?.let {
                val voltageDropChart =
                    project.powerChartList[powerChartIndex.toInt()].voltageDropChartList[index.toInt()]
                _screenState.value = _screenState.value.copy(
                    inputVoltageDrop = voltageDropChart.voltageDrop,
                    inputLength = voltageDropChart.length,
                    inputArea = voltageDropChart.area,
                    inputCoefficient = voltageDropChart.coefficient,
                    isSingle = voltageDropChart.isSingle,
                    inputPower = power
                )
            }
        }
    }

    private suspend fun getProjectById(projectId: String): Project? {

        return projectUseCases.getProject(projectId)
    }

    private suspend fun getPower(index: String): String {
        val project = getProjectById(projectId)
        project?.let { p ->
            val currentPowerChart = p.powerChartList[powerChartIndex.toInt()]
            val mainColumnPower = p.mainColonPower
            val columnPower = currentPowerChart.colonPower
            val biggestPower = currentPowerChart.loadChart.biggestMomentumLine
            return when (index) {
                "0" -> mainColumnPower
                "1" -> columnPower
                "2" -> biggestPower
                else -> ""
            }
        }
        return ""
    }

    fun onEvent(event: CalculatorEvent) {
        when (event) {
            is CalculatorEvent.PhaseToggleClicked -> {
                if (event.isSingle) {
                    _screenState.value = _screenState.value.copy(
                        isSingle = true
                    )
                } else {
                    _screenState.value = _screenState.value.copy(
                        isSingle = false
                    )
                }
            }

            is CalculatorEvent.AreaValueEntered -> {
                _screenState.value = _screenState.value.copy(
                    inputArea = event.value
                )
            }

            is CalculatorEvent.CoefficientValueEntered -> {
                _screenState.value = _screenState.value.copy(
                    inputCoefficient = event.value
                )
            }

            is CalculatorEvent.LengthValueEntered -> {
                _screenState.value = _screenState.value.copy(
                    inputLength = event.value
                )
            }

            is CalculatorEvent.VoltageDropValueEntered -> {
                _screenState.value = _screenState.value.copy(
                    inputVoltageDrop = event.value
                )
            }

            CalculatorEvent.CalculateAreaClicked -> {
                viewModelScope.launch {
                    val area = calculateCableSize(
                        isSingle = _screenState.value.isSingle,
                        power = _screenState.value.inputPower,
                        length = _screenState.value.inputLength,
                        voltageDrop = _screenState.value.inputVoltageDrop,
                        coefficient = _screenState.value.inputCoefficient,
                    )
                    _screenState.value = _screenState.value.copy(
                        inputArea = area
                    )
                    val newVoltageDropChart = VoltageDropChart(
                        voltageDrop = _screenState.value.inputVoltageDrop,
                        length = _screenState.value.inputLength,
                        watt = _screenState.value.inputPower,
                        isSingle = _screenState.value.isSingle,
                        coefficient = _screenState.value.inputCoefficient,
                        area = _screenState.value.inputArea,
                    )
                    val currentProject = getProjectById(projectId)
                    currentProject?.let { project ->
                        val updatedPowerChartList = project.powerChartList.toMutableList()
                        updatedPowerChartList[powerChartIndex.toInt()].voltageDropChartList[index.toInt()] =
                            newVoltageDropChart
                        val updatedProject = project.copy(powerChartList = updatedPowerChartList)
                        projectUseCases.updateProject(updatedProject)
                    }
                    _uiEventFlow.emit(UIEvent.ShowMessage("Cable size calculated"))
                }
            }

            CalculatorEvent.CalculateVoltageDropClicked -> {
                viewModelScope.launch {
                    val voltageDrop = calculateVoltageDrop(
                        isSingle = _screenState.value.isSingle,
                        power = _screenState.value.inputPower,
                        length = _screenState.value.inputLength,
                        area = _screenState.value.inputArea,
                        coefficient = _screenState.value.inputCoefficient,
                    )
                    _screenState.value = _screenState.value.copy(
                        inputVoltageDrop = voltageDrop
                    )
                    val newVoltageDropChart = VoltageDropChart(
                        voltageDrop = _screenState.value.inputVoltageDrop,
                        length = _screenState.value.inputLength,
                        watt = _screenState.value.inputPower,
                        isSingle = _screenState.value.isSingle,
                        coefficient = _screenState.value.inputCoefficient,
                        area = _screenState.value.inputArea,
                    )
                    val currentProject = getProjectById(projectId)
                    currentProject?.let { project ->
                        val updatedPowerChartList = project.powerChartList.toMutableList()
                        updatedPowerChartList[powerChartIndex.toInt()].voltageDropChartList[index.toInt()] =
                            newVoltageDropChart
                        val updatedProject = project.copy(powerChartList = updatedPowerChartList)
                        projectUseCases.updateProject(updatedProject)
                    }
                    _uiEventFlow.emit(UIEvent.ShowMessage("VoltageDrop calculated"))
                }
            }
        }
    }

    private fun calculateCableSize(
        isSingle: Boolean,
        power: String,
        length: String,
        voltageDrop: String,
        coefficient: String,
    ): String {
        val result = if (isSingle) {
            (200.0 * power.toDouble() * length.toDouble()) / (coefficient.toDouble() * voltageDrop.toDouble() * 220.0 * 220.0)
        } else {
            (100.0 * power.toDouble() * length.toDouble()) / (coefficient.toDouble() * voltageDrop.toDouble() * 380.0 * 380.0)
        }
        return String.format("%.3f", result)
    }

    private fun calculateVoltageDrop(
        isSingle: Boolean,
        power: String,
        length: String,
        area: String,
        coefficient: String,
    ): String {
        val result = if (isSingle) {
            (200.0 * power.toDouble() * length.toDouble()) / (coefficient.toDouble() * area.toDouble() * 220.0 * 220.0)
        } else {
            (100.0 * power.toDouble() * length.toDouble()) / (coefficient.toDouble() * area.toDouble() * 380.0 * 380.0)
        }
        return String.format("%.3f", result)
    }
}