package com.baris.voltagedropapp.feature_voltage_drop.presentation.calculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.baris.voltagedropapp.R
import com.baris.voltagedropapp.feature_voltage_drop.presentation.utils.UIEvent
import com.baris.voltagedropapp.feature_voltage_drop.presentation.utils.components.CustomTextField
import com.baris.voltagedropapp.feature_voltage_drop.presentation.utils.components.CustomTopBar
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    navController: NavController,
) {
    val screenState = viewModel.screenState.value
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.uiEventFlow.collectLatest { event ->
            when (event) {
                is UIEvent.ShowMessage -> {
                    snackBarHostState.showSnackbar(
                        message = event.message
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CustomTopBar(title = "CalculatorScreen", showIcon = true) {
                navController.popBackStack()
            }
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(15.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = screenState.isSingle,
                    onCheckedChange = { newValue ->
                        viewModel.onEvent(CalculatorEvent.PhaseToggleClicked(newValue))
                    }
                )
                Text(
                    modifier = Modifier.padding(end = 10.dp),
                    text = if (screenState.isSingle) stringResource(id = R.string.single_phase) else stringResource(
                        id = R.string.three_phase
                    )
                )
            }
            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(modifier = Modifier.width(90.dp)) {
                    CustomTextField(
                        value = screenState.inputVoltageDrop,
                        onValueChange = { newValue ->
                            viewModel.onEvent(CalculatorEvent.VoltageDropValueEntered(newValue))
                        },
                        label = "e%"
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CustomTextField(
                            value = screenState.inputLength,
                            onValueChange = { newValue ->
                                viewModel.onEvent(CalculatorEvent.LengthValueEntered(newValue))
                                //length = newValue.filter { it.isDigit() }
                            },
                            label = "L"
                        )
                        Text(
                            modifier = Modifier.padding(start = 4.dp, top = 16.dp, end = 4.dp),
                            text = "*"
                        )
                        Text(
                            modifier = Modifier.padding(start = 4.dp, top = 16.dp, end = 4.dp),
                            text = screenState.inputPower
                        )
                        Text(
                            modifier = Modifier.padding(start = 4.dp, top = 16.dp, end = 4.dp),
                            text = "*"
                        )
                        Text(
                            modifier = Modifier.padding(top = 16.dp),
                            text = if (screenState.isSingle) "200" else "100"
                            //text = screenState.singleOrThreeState
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "=")
                        Divider(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .height(2.dp)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CustomTextField(
                            value = screenState.inputCoefficient,
                            onValueChange = { newValue ->
                                viewModel.onEvent(CalculatorEvent.CoefficientValueEntered(newValue))
                            },
                            label = "K"
                        )
                        Text(
                            modifier = Modifier.padding(start = 4.dp, top = 16.dp, end = 4.dp),
                            text = "*"
                        )
                        CustomTextField(
                            value = screenState.inputArea,
                            onValueChange = { newValue ->
                                viewModel.onEvent(CalculatorEvent.AreaValueEntered(newValue))
                            },
                            label = "S"
                        )
                        Text(
                            modifier = Modifier.padding(start = 4.dp, top = 16.dp, end = 4.dp),
                            text = "*"
                        )
                        Text(
                            modifier = Modifier.padding(top = 16.dp),
                            text = if (screenState.isSingle) "220" else "380"
                            //text = screenState.inputVoltage
                        )
                        Text(
                            modifier = Modifier.padding(top = 10.dp),
                            text = "2",
                            style = TextStyle(baselineShift = BaselineShift.Superscript)
                        )
                    }
                }
            }
            Button(onClick = {
                if (screenState.inputLength.isNotBlank() && screenState.inputCoefficient.isNotBlank()) {
                    if (screenState.inputVoltageDrop.isNotBlank() && screenState.inputArea.isBlank()) {
                        viewModel.onEvent(CalculatorEvent.CalculateAreaClicked)
                    } else if (screenState.inputArea.isNotBlank() && screenState.inputVoltageDrop.isBlank()) {
                        viewModel.onEvent(CalculatorEvent.CalculateVoltageDropClicked)
                    }
                }
            }) {
                Text(text = stringResource(id = R.string.calculate))
            }
        }
    }
}