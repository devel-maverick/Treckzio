package com.weathersnap.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.weathersnap.app.ui.camera.CameraScreen
import com.weathersnap.app.ui.report.CreateReportScreen
import com.weathersnap.app.ui.reports.ReportsScreen
import com.weathersnap.app.ui.weather.WeatherScreen

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Routes.Weather
    ) {
        composable(Routes.Weather) {
            WeatherScreen(
                onCreateReport = { navController.navigate(Routes.CreateReport) },
                onOpenReports = { navController.navigate(Routes.SavedReports) }
            )
        }
        composable(Routes.CreateReport) {
            CreateReportScreen(
                onBack = { navController.popBackStack() },
                onOpenCamera = { navController.navigate(Routes.Camera) },
                onSaved = {
                    // Go to reports after saving
                    navController.navigate(Routes.SavedReports) {
                        popUpTo(Routes.Weather) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Routes.Camera) {
            CameraScreen(
                onClose = { navController.popBackStack() },
                onCaptured = { navController.popBackStack() }
            )
        }
        composable(Routes.SavedReports) {
            ReportsScreen(onBack = { navController.popBackStack() })
        }
    }
}
