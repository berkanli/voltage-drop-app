package com.baris.voltagedropapp.feature_voltage_drop.presentation.power_chart

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.baris.voltagedropapp.R
import com.baris.voltagedropapp.feature_voltage_drop.domain.model.PowerChart
import com.baris.voltagedropapp.feature_voltage_drop.presentation.navigation.AppScreens
import com.baris.voltagedropapp.feature_voltage_drop.presentation.utils.components.CustomTopBar
import com.baris.voltagedropapp.feature_voltage_drop.presentation.utils.UIEvent
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PowerChartScreen(
    viewModel: PowerChartViewModel,
    navController: NavController,
    projectId: String
) {
    val screenState = viewModel.screenState.value
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = projectId) {
        viewModel.getProjectById(projectId)
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
            CustomTopBar(title = "PowerChartScreen", showIcon = true) {
                navController.popBackStack()
            }
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = screenState.inputDescription,
                onValueChange = { newValue ->
                    viewModel.onEvent(PowerChartEvent.DescriptionValueEntered(newValue))
                },
                label = { Text(text = stringResource(id = R.string.description)) }
            )
            OutlinedTextField(
                value = screenState.inputAmount,
                onValueChange = { newValue ->
                    viewModel.onEvent(PowerChartEvent.AmountValueEntered(newValue))
                },
                label = { Text(text = stringResource(id = R.string.amount)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = screenState.inputPercent,
                onValueChange = { newValue ->
                    viewModel.onEvent(PowerChartEvent.PercentValueEntered(newValue))
                },
                label = { Text(text = stringResource(id = R.string.percent)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
            Button(onClick = {
                viewModel.onEvent(PowerChartEvent.CreateButtonClicked)
            }) {
                Text(text = stringResource(id = R.string.create))
            }
            Text(text = "Main Column Line Power: ${screenState.mainColumnPowerState} ")
            LazyColumn {
                itemsIndexed(screenState.powerChartList) { index, powerChart ->
                    PowerChartItem(
                        viewModel = viewModel,
                        navController = navController,
                        powerChart = powerChart,
                        index = index,
                        projectId = projectId
                    )
                }
            }
        }
    }
}

@Composable
fun PowerChartItem(
    viewModel: PowerChartViewModel,
    navController: NavController,
    powerChart: PowerChart,
    index: Int,
    projectId: String
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate("${AppScreens.LoadChartScreen.name}/${projectId}?powerChartIndex=${index}")
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Description: ${powerChart.description}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Amount: ${powerChart.amount}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Percent: ${powerChart.percent}%")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Installed Power: ${powerChart.loadChart.installedPower}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Demand Power: ${powerChart.loadChart.demandPower}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Colon Power: ${powerChart.colonPower}")
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier.height(200.dp),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                IconButton(onClick = {
                    navController.navigate("${AppScreens.VoltageDropScreen.name}/${projectId}?powerChartIndex=${index}")
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "VoltageDrop"
                    )
                }
                IconButton(onClick = {
                    viewModel.onEvent(PowerChartEvent.DeleteButtonClicked(index))
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete PowerChart"
                    )
                }
            }
        }
    }
}