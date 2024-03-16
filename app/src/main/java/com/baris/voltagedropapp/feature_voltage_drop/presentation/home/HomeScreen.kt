package com.baris.voltagedropapp.feature_voltage_drop.presentation.home

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.baris.voltagedropapp.feature_voltage_drop.data.utils.formatDate
import com.baris.voltagedropapp.feature_voltage_drop.domain.model.Project
import com.baris.voltagedropapp.feature_voltage_drop.presentation.navigation.AppScreens
import com.baris.voltagedropapp.feature_voltage_drop.presentation.utils.UIEvent
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel, navController: NavController) {

    val screenState = viewModel.screenState
    val projectList = screenState.value.projectList?.collectAsState(initial = emptyList())
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
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = screenState.value.inputTitleValue,
                onValueChange = { newValue ->
                    viewModel.onEvent(HomeEvent.ValueEntered(newValue))
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            if (screenState.value.isAddButtonVisible) {
                Button(onClick = {
                    viewModel.onEvent(
                        HomeEvent.AddButtonClicked(
                            project = Project(
                                title = screenState.value.inputTitleValue
                            )
                        )
                    )
                }) {
                    Text(text = "Add Project")
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            LazyColumn {
                items(projectList?.value ?: emptyList()) { project ->
                    ProjectItem(
                        viewModel = viewModel,
                        project = project,
                        navController = navController
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }
}

@Composable
fun ProjectItem(viewModel: HomeViewModel, project: Project, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .clickable {
                navController.navigate("${AppScreens.PowerChartScreen.name}/${project.id}")
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column {
                Text(
                    text = project.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = formatDate(project.entryDate.time))
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = {
                viewModel.onEvent(HomeEvent.DeleteProject(project))
            }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "")
            }
        }
    }
}