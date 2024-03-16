package com.baris.voltagedropapp.feature_voltage_drop.presentation.power_chart

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baris.voltagedropapp.feature_voltage_drop.domain.model.PowerChart
import com.baris.voltagedropapp.feature_voltage_drop.domain.model.Project
import com.baris.voltagedropapp.feature_voltage_drop.domain.use_case.ProjectUseCases
import com.baris.voltagedropapp.feature_voltage_drop.presentation.utils.UIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PowerChartViewModel @Inject constructor(
    private val projectUseCases: ProjectUseCases,
    savedStateHandle: SavedStateHandle
) :
    ViewModel() {
    private val _screenState = mutableStateOf(PowerChartState())
    val screenState: State<PowerChartState> = _screenState

    private val _uiEventFlow = MutableSharedFlow<UIEvent>()
    val uiEventFlow = _uiEventFlow.asSharedFlow()

    private val projectId: String = savedStateHandle.get<String>("projectId") ?: ""

    init {
        viewModelScope.launch {
            val project = projectUseCases.getProject(projectId)
            calculateColumnPowerAndUpdateProject(project = project)

            _screenState.value = _screenState.value.copy(
                powerChartList = project?.powerChartList ?: emptyList()
            )
        }
    }

    private fun calculateColumnPowerAndUpdateProject(project: Project?) {
        project?.let { p ->
            val updatedPowerChartList = p.powerChartList.toMutableList().map { powerChart ->
                var columnPower = 0.0
                val demandPower = powerChart.loadChart.demandPower.toDoubleOrNull() ?: 0.0
                val amount = powerChart.amount.toDoubleOrNull() ?: 0.0
                val percent = powerChart.percent.toDoubleOrNull() ?: 0.0
                columnPower += (demandPower * amount * percent) / 100.0
                powerChart.copy(
                    colonPower = String.format("%.3f", columnPower)
                )
            }
            val updatedProject = p.copy(powerChartList = updatedPowerChartList.toMutableList())
            val mainColumnPower = calculateMainPower(updatedProject)

            viewModelScope.launch {
                projectUseCases.updateProject(updatedProject.copy(mainColonPower = mainColumnPower))
                _screenState.value = _screenState.value.copy(
                    powerChartList = updatedPowerChartList,
                    mainColumnPowerState = mainColumnPower
                )
            }
        }
    }

    private fun calculateMainPower(project: Project): String {
        var mainColumnPower = 0.0
        project.powerChartList.forEach { powerChart ->
            val demandPower = powerChart.loadChart.demandPower.toDoubleOrNull()
            val amount = powerChart.amount.toDoubleOrNull()
            val percent = powerChart.percent.toDoubleOrNull()

            if (demandPower != null && amount != null && percent != null) {
                mainColumnPower += (demandPower * amount * percent) / 100
            } else {
                Log.e("PowerChartViewModel", "Invalid data for powerChart: $powerChart")
            }
        }
        return String.format("%.3f", mainColumnPower)
    }

    suspend fun getProjectById(projectId: String): Project? {
        return projectUseCases.getProject(projectId)
    }

    private suspend fun addPowerChartAndUpdateProject(projectId: String, powerChart:PowerChart){
        val currentProject = getProjectById(projectId)
         currentProject?.let { project ->
            val updatedPowerChartList = project.powerChartList.toMutableList()
             updatedPowerChartList.add(powerChart)
            projectUseCases.updateProject(project.copy(powerChartList = updatedPowerChartList))
             _screenState.value = _screenState.value.copy(
                 powerChartList = updatedPowerChartList,
                 inputDescription = "",
                 inputAmount = "",
                 inputPercent = "",
             )
        }
    }

    private suspend fun deletePowerChartAndUpdateProject(projectId: String, index:Int){
        val currentProject = getProjectById(projectId)
        currentProject?.let { project ->
            val updatedPowerChartList = project.powerChartList.toMutableList()
            updatedPowerChartList.removeAt(index)
            projectUseCases.updateProject(project.copy(powerChartList = updatedPowerChartList))
            _screenState.value = _screenState.value.copy(
                powerChartList = updatedPowerChartList,
            )
        }
    }

    fun onEvent(event: PowerChartEvent) {
        when (event) {
            is PowerChartEvent.AmountValueEntered -> {
                _screenState.value = _screenState.value.copy(
                    inputAmount = event.enteredValue
                )
            }

            is PowerChartEvent.DescriptionValueEntered -> {
                _screenState.value = _screenState.value.copy(
                    inputDescription = event.enteredValue
                )
            }

            is PowerChartEvent.PercentValueEntered -> {
                _screenState.value = _screenState.value.copy(
                    inputPercent = event.enteredValue
                )
            }

            PowerChartEvent.CreateButtonClicked -> {
                viewModelScope.launch {
                    val newPowerChart = PowerChart(
                        description = _screenState.value.inputDescription,
                        amount = _screenState.value.inputAmount,
                        percent = _screenState.value.inputPercent
                    )
                    addPowerChartAndUpdateProject(projectId, newPowerChart)
                    _uiEventFlow.emit(UIEvent.ShowMessage("PowerChart created successfully"))
                }
            }

            is PowerChartEvent.DeleteButtonClicked -> {
                viewModelScope.launch {
                    deletePowerChartAndUpdateProject(projectId, event.index)
                    _uiEventFlow.emit(UIEvent.ShowMessage("PowerChart deleted successfully"))
                }
            }
        }
    }
}