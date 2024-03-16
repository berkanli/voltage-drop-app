package com.baris.voltagedropapp.feature_voltage_drop.presentation.voltage_drop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.baris.voltagedropapp.R
import com.baris.voltagedropapp.feature_voltage_drop.domain.model.PowerChart
import com.baris.voltagedropapp.feature_voltage_drop.domain.model.Project
import com.baris.voltagedropapp.feature_voltage_drop.domain.model.VoltageDropChart
import com.baris.voltagedropapp.feature_voltage_drop.presentation.navigation.AppScreens
import com.baris.voltagedropapp.feature_voltage_drop.presentation.utils.components.CustomTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoltageDropScreen(
    viewModel: VoltageDropViewModel,
    navController: NavController,
    projectId: String,
    powerChartIndex: String
) {
    val screenState = viewModel.screenState.value
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            CustomTopBar(title = "VoltageDropScreen", showIcon = true) {
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
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = screenState.descriptionState,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp)
            )
            Spacer(modifier = Modifier.height(15.dp))

            VoltageDropText(screenState)

            LazyColumn {
                itemsIndexed(screenState.voltageDropList) { index, voltageDropChart ->
                    VoltageDropLineRow(
                        project = screenState.project,
                        powerChart = screenState.powerChartState,
                        voltageDropChart = voltageDropChart,
                        index = index,
                        onNoteClicked = {
                            navController.navigate("${AppScreens.CalculatorScreen.name}/${projectId}?powerChartIndex=${powerChartIndex}&index=${index}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun VoltageDropLineRow(
    project: Project,
    powerChart: PowerChart,
    voltageDropChart: VoltageDropChart,
    index: Int,
    onNoteClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (index) {
            0 -> Text(
                modifier = Modifier.weight(1f),
                text = stringResource(id = R.string.main_column_line)
            )

            1 -> Text(
                modifier = Modifier.weight(1f),
                text = stringResource(id = R.string.column_line)
            )

            2 -> Text(
                modifier = Modifier.weight(1f),
                text = stringResource(id = R.string.line)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Length: ${voltageDropChart.length} m")
            Spacer(modifier = Modifier.width(16.dp))
            when (index) {
                0 -> Text(text = "Power: ${project.mainColonPower} watt")

                1 -> Text(text = "Power: ${powerChart.colonPower} watt")

                2 -> Text(text = "Power: ${powerChart.loadChart.biggestMomentumLine} watt")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Cable Size: ${voltageDropChart.area} mm2")
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Voltage Drop: ${voltageDropChart.voltageDrop}%")
        }
        IconButton(onClick = onNoteClicked) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Line"
            )
        }
    }
}

@Composable
fun VoltageDropText(screenState: VoltageDropState) {
    val voltageDrop = screenState.voltageDropState.toDouble()
    val color = if (voltageDrop < 1.5) Color.Green else Color.Red
    val angleBracket = if (voltageDrop < 1.5) "<" else ">"

    Text(
        text = buildAnnotatedString {
            append("Total voltage drop: ")
            pushStyle(SpanStyle(color = color))
            append("$voltageDrop")
            pop()
            append(" $angleBracket 1.5%")
        }
    )
}