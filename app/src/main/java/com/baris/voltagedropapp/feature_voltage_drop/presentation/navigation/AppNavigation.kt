package com.baris.voltagedropapp.feature_voltage_drop.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.baris.voltagedropapp.feature_voltage_drop.presentation.calculator.CalculatorScreen
import com.baris.voltagedropapp.feature_voltage_drop.presentation.calculator.CalculatorViewModel
import com.baris.voltagedropapp.feature_voltage_drop.presentation.home.HomeScreen
import com.baris.voltagedropapp.feature_voltage_drop.presentation.home.HomeViewModel
import com.baris.voltagedropapp.feature_voltage_drop.presentation.load_chart.LoadChartScreen
import com.baris.voltagedropapp.feature_voltage_drop.presentation.load_chart.LoadChartViewModel
import com.baris.voltagedropapp.feature_voltage_drop.presentation.power_chart.PowerChartScreen
import com.baris.voltagedropapp.feature_voltage_drop.presentation.power_chart.PowerChartViewModel
import com.baris.voltagedropapp.feature_voltage_drop.presentation.voltage_drop.VoltageDropScreen
import com.baris.voltagedropapp.feature_voltage_drop.presentation.voltage_drop.VoltageDropViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.HomeScreen.name) {

        composable(AppScreens.HomeScreen.name) {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(viewModel = viewModel, navController = navController)
        }

        composable(
            route = AppScreens.CalculatorScreen.name + "/{projectId}?powerChartIndex={powerChartIndex}&index={index}",
            arguments = listOf(
                navArgument(name = "projectId") { type = NavType.StringType },
                navArgument(name = "powerChartIndex") { type = NavType.StringType },
                navArgument(name = "index") { type = NavType.StringType },
            )
        ) {
            val viewModel: CalculatorViewModel = hiltViewModel()
            CalculatorScreen(
                viewModel = viewModel,
                navController = navController,
            )
        }

        composable(
            route = AppScreens.PowerChartScreen.name + "/{projectId}",
            arguments = listOf(
                navArgument(name = "projectId") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val viewModel: PowerChartViewModel = hiltViewModel()
            val id = backStackEntry.arguments?.getString("projectId") ?: ""
            PowerChartScreen(
                viewModel = viewModel,
                navController = navController,
                projectId = id,
            )
        }

        composable(
            route = AppScreens.LoadChartScreen.name + "/{projectId}?powerChartIndex={powerChartIndex}",
            arguments = listOf(
                navArgument(name = "projectId") { type = NavType.StringType },
                navArgument(name = "powerChartIndex") { type = NavType.StringType }
            )
        ) {
            val viewModel: LoadChartViewModel = hiltViewModel()
            LoadChartScreen(
                viewModel = viewModel,
                navController = navController,
            )
        }

        composable(
            route = AppScreens.VoltageDropScreen.name + "/{projectId}?powerChartIndex={powerChartIndex}",
            arguments = listOf(
                navArgument(name = "projectId") { type = NavType.StringType },
                navArgument(name = "powerChartIndex") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val viewModel: VoltageDropViewModel = hiltViewModel()
            val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
            val powerChartIndex = backStackEntry.arguments?.getString("powerChartIndex") ?: ""
            VoltageDropScreen(
                viewModel = viewModel,
                navController = navController,
                projectId = projectId,
                powerChartIndex = powerChartIndex,
            )
        }
    }
}