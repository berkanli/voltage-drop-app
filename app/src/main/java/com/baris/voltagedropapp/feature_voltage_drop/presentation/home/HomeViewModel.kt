package com.baris.voltagedropapp.feature_voltage_drop.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baris.voltagedropapp.feature_voltage_drop.domain.use_case.ProjectUseCases
import com.baris.voltagedropapp.feature_voltage_drop.presentation.utils.UIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val projectUseCases: ProjectUseCases) :
    ViewModel() {

    private val _screenState = mutableStateOf(HomeState())
    val screenState: State<HomeState> = _screenState

    private val _uiEventFlow = MutableSharedFlow<UIEvent>()
    val uiEventFlow = _uiEventFlow.asSharedFlow()

    init {
        getProjects()
    }

    private fun getProjects() {
        _screenState.value = _screenState.value.copy(
            projectList = projectUseCases.getProjects.invoke()
        )
    }

    fun onEvent(event: HomeEvent) {
        when (event) {

            is HomeEvent.AddButtonClicked -> {
                viewModelScope.launch {
                    projectUseCases.addProject(event.project)
                    _uiEventFlow.emit(UIEvent.ShowMessage("Project added successfully"))
                }
                _screenState.value = _screenState.value.copy(
                    inputTitleValue = "",
                    isAddButtonVisible = false
                )
            }

            is HomeEvent.DeleteProject -> {
                viewModelScope.launch {
                    projectUseCases.deleteProject(event.project)
                    _uiEventFlow.emit(UIEvent.ShowMessage("Project removed successfully"))
                }
            }

            is HomeEvent.ValueEntered -> {
                _screenState.value = _screenState.value.copy(
                    inputTitleValue = event.enteredValue,
                    isAddButtonVisible = true
                )
            }
        }
    }
}