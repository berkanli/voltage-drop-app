package com.baris.voltagedropapp.feature_voltage_drop.presentation.load_chart

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.baris.voltagedropapp.feature_voltage_drop.domain.model.Line
import com.baris.voltagedropapp.feature_voltage_drop.presentation.utils.UIEvent
import com.baris.voltagedropapp.feature_voltage_drop.presentation.utils.components.CustomTopBar
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadChartScreen(
    viewModel: LoadChartViewModel,
    navController: NavController,
) {
    val screenState = viewModel.screenState.value
    val snackBarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current
    val portrait = remember { mutableStateOf(true) }
    val activity = context as Activity
    if (portrait.value) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    } else {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
    portrait.value = !portrait.value

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
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            CustomTopBar(title = "LoadChartScreen", showIcon = true) {
                navController.popBackStack()
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Spacer(modifier = Modifier.weight(0.05f))
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Outlet")
                    OutlinedTextField(
                        value = screenState.inputOutlet,
                        onValueChange = { newValue ->
                            viewModel.onEvent(LoadChartEvent.OutletValueEntered(newValue))
                        }
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Circuit Breaker")
                    OutlinedTextField(
                        value = screenState.inputCircuitBreaker,
                        onValueChange = { newValue ->
                            viewModel.onEvent(LoadChartEvent.CircuitBreakerValueEntered(newValue))
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Phase Balance")
                    OutlinedTextField(
                        value = screenState.inputPhaseBalance,
                        onValueChange = { newValue ->
                            viewModel.onEvent(LoadChartEvent.PhaseBalanceValueEntered(newValue))
                        }
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Power")
                    OutlinedTextField(
                        value = screenState.inputPower,
                        onValueChange = { newValue ->
                            viewModel.onEvent(LoadChartEvent.PowerValueEntered(newValue))
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Description")
                    OutlinedTextField(
                        value = screenState.inputDescription,
                        onValueChange = { newValue ->
                            viewModel.onEvent(LoadChartEvent.DescriptionValueEntered(newValue))
                        },
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {}
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .width(150.dp)
                            .height(40.dp),
                        onClick = { viewModel.onEvent(LoadChartEvent.CreateLineButtonClicked) }
                    ) {
                        Text(text = "Create")
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        modifier = Modifier
                            .padding(top = 4.dp),
                        text = "The biggest power = ${screenState.biggestPower}"
                    )
                }
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                itemsIndexed(screenState.lineList) { index, line ->
                    LineRow(screenState, line, index, viewModel)
                    Divider(Modifier.height(2.dp))
                }
            }
        }
    }
}

@Composable
fun LineRow(screenState: LoadChartState, line: Line, index: Int, viewModel: LoadChartViewModel) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(0.2f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = line.lineNumber.toString())
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = line.outlet)
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = line.circuitBreaker)
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = line.phaseBalance)
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = line.power)
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = line.description)
            }
            Column(
                modifier = Modifier.weight(0.3f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (index == screenState.lineList.size - 1) {
                    IconButton(onClick = {
                        viewModel.onEvent(LoadChartEvent.DeleteLineButtonClicked(index))
                    }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Line")
                    }
                }
            }
        }
    }
}