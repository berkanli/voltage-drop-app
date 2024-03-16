package com.baris.voltagedropapp.feature_voltage_drop.presentation.load_chart

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baris.voltagedropapp.feature_voltage_drop.domain.model.Line
import com.baris.voltagedropapp.feature_voltage_drop.domain.model.Project
import com.baris.voltagedropapp.feature_voltage_drop.domain.use_case.ProjectUseCases
import com.baris.voltagedropapp.feature_voltage_drop.presentation.utils.UIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoadChartViewModel @Inject constructor(
    private val projectUseCases: ProjectUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _screenState = mutableStateOf(LoadChartState())
    val screenState: State<LoadChartState> = _screenState

    private val _uiEventFlow = MutableSharedFlow<UIEvent>()
    val uiEventFlow = _uiEventFlow.asSharedFlow()

    private val projectId: String = savedStateHandle.get<String>("projectId") ?: ""
    private val powerChartIndex: String = savedStateHandle.get<String>("powerChartIndex") ?: ""

    init {
        viewModelScope.launch {
            val project = projectUseCases.getProject(projectId)
            val currentPowerChart = project?.powerChartList?.get(powerChartIndex.toInt())
            val lineList = currentPowerChart?.loadChart?.lineList ?: emptyList()
            _screenState.value = _screenState.value.copy(
                lineList = lineList,
                biggestPower = getBiggestPower(lineList)
            )
            updateInstalledAndDemandPower(_screenState.value.lineList)
        }
    }

    suspend fun getProjectById(projectId: String): Project? {
        return projectUseCases.getProject(projectId)
    }

    fun onEvent(event: LoadChartEvent) {
        when (event) {

            LoadChartEvent.CreateLineButtonClicked -> {
                viewModelScope.launch {
                    val newLine = Line(
                        lineNumber = (_screenState.value.lineList.size + 1),
                        outlet = _screenState.value.inputOutlet,
                        circuitBreaker = _screenState.value.inputCircuitBreaker,
                        phaseBalance = _screenState.value.inputPhaseBalance,
                        power = _screenState.value.inputPower,
                        description = _screenState.value.inputDescription
                    )
                    getNewLineAndUpdateProject(projectId, powerChartIndex.toInt(), newLine )
                    _uiEventFlow.emit(UIEvent.ShowMessage("A new line created"))
                }
            }

            is LoadChartEvent.DeleteLineButtonClicked -> {
                if (_screenState.value.lineList.size == event.index + 1) {
                    viewModelScope.launch {
                        deleteLineAndUpdateProject(projectId, powerChartIndex.toInt(), event.index)
                        _uiEventFlow.emit(UIEvent.ShowMessage("A line created"))
                    }
                }
            }

            is LoadChartEvent.CircuitBreakerValueEntered -> {
                _screenState.value = _screenState.value.copy(
                    inputCircuitBreaker = event.enteredValue
                )
            }

            is LoadChartEvent.DescriptionValueEntered -> {
                _screenState.value = _screenState.value.copy(
                    inputDescription = event.enteredValue
                )
            }

            is LoadChartEvent.OutletValueEntered -> {
                _screenState.value = _screenState.value.copy(
                    inputOutlet = event.enteredValue
                )
            }

            is LoadChartEvent.PhaseBalanceValueEntered -> {
                _screenState.value = _screenState.value.copy(
                    inputPhaseBalance = event.enteredValue
                )
            }

            is LoadChartEvent.PowerValueEntered -> {
                _screenState.value = _screenState.value.copy(
                    inputPower = event.enteredValue
                )
            }
        }
    }

    private suspend fun deleteLineAndUpdateProject(projectId: String, powerChartIndex: Int, index: Int) {
        val currentProject = getProjectById(projectId)
        currentProject?.let { project ->
            val updatedLineList =
                project.powerChartList[powerChartIndex].loadChart.lineList.toMutableList()
            updatedLineList.removeAt(index)
            val updatedPowerChart =
                project.powerChartList[powerChartIndex].copy(
                    loadChart = project.powerChartList[powerChartIndex].loadChart.copy(
                        lineList = updatedLineList,
                        biggestMomentumLine = getBiggestPower(updatedLineList)
                    )
                )
            val updatedProject = project.copy(
                powerChartList = project.powerChartList.toMutableList().apply {
                    set(powerChartIndex, updatedPowerChart)
                }
            )
            projectUseCases.updateProject(updatedProject)
            _screenState.value = _screenState.value.copy(
                lineList = updatedLineList,
                biggestPower = getBiggestPower(updatedLineList)
            )
            updateInstalledAndDemandPower(updatedLineList)
        }
    }

    private suspend fun getNewLineAndUpdateProject(projectId: String, powerChartIndex: Int, line: Line) {
        val currentProject = getProjectById(projectId)
        currentProject?.let { project ->
            val updatedLineList =
                (project.powerChartList[powerChartIndex].loadChart.lineList).toMutableList()
            updatedLineList.add(line)
            val updatedPowerChart =
                project.powerChartList[powerChartIndex].copy(
                    loadChart = project.powerChartList[powerChartIndex].loadChart.copy(
                        lineList = updatedLineList,
                        biggestMomentumLine = getBiggestPower(updatedLineList)
                    )
                )
            val updatedProject = project.copy(
                powerChartList = project.powerChartList.toMutableList().apply {
                    set(powerChartIndex, updatedPowerChart)
                }
            )
            projectUseCases.updateProject(updatedProject)

            _screenState.value = _screenState.value.copy(
                inputLineNumber = (_screenState.value.lineList.size + 1).toString(),
                lineList = updatedLineList,
                biggestPower = getBiggestPower(updatedLineList),
                inputOutlet = "",
                inputCircuitBreaker = "",
                inputPower = "",
                inputPhaseBalance = "",
                inputDescription = ""
            )
            updateInstalledAndDemandPower(updatedLineList)
        }
    }

    private fun updateInstalledAndDemandPower(lineList: List<Line>) {
        viewModelScope.launch {
            val installedPower = lineList.sumBy { it.power.toInt() }.toString()
            val demandPower = calculateDemandPower(installedPower)
            val project = getProjectById(projectId)
            project?.let { p ->
                viewModelScope.launch {
                    val currentPowerChart = p.powerChartList[powerChartIndex.toInt()]
                    val updatedLoadChart = currentPowerChart.loadChart.copy(
                        installedPower = installedPower,
                        demandPower = demandPower
                    )
                    val updatedPowerChart = currentPowerChart.copy(
                        loadChart = updatedLoadChart
                    )
                    val updatedProject = p.copy(
                        powerChartList = p.powerChartList.toMutableList().apply {
                            set(powerChartIndex.toInt(), updatedPowerChart)
                        }
                    )
                    projectUseCases.updateProject(updatedProject)
                }
            }
        }
    }

    private fun calculateDemandPower(installedPower: String): String {
        var demandPower: Double
        val power = installedPower.toIntOrNull()
            ?: 0 // Attempt to convert to integer, default to 0 if conversion fails
        if (power > 8000) {
            demandPower = (8000 * 60) / 100.0
            demandPower += ((power - 8000.0) * 40) / 100.0
        } else {
            demandPower = (power * 60) / 100.0
        }
        return String.format("%.3f", demandPower)
    }

    private fun getBiggestPower(lineList: List<Line>): String {
        var biggestPower = 0.0
        lineList.forEach { line ->
            val maxPower = maxOf(line.power.toDoubleOrNull() ?: 0.0, biggestPower)
            biggestPower = maxPower
        }
        return biggestPower.toString()
    }
}