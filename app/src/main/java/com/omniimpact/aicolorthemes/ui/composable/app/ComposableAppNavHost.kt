package com.omniimpact.aicolorthemes.ui.composable.app

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.omniimpact.aicolorthemes.ui.composable.home.ComposableHomeScreen
import com.omniimpact.aicolorthemes.ui.composable.picker.ComposablePickerScreen
import com.omniimpact.aicolorthemes.ui.composable.settings.ComposableSettingsScreen
import com.omniimpact.aicolorthemes.viewmodel.settings.ViewModelSettings

enum class Screen(val route: String, val title: String) {
	Home("home", "Home"),
	Picker("picker", "Picker"),
	Settings("settings", "Settings")
}

@Composable
fun ComposableAppNavHost(
	viewModelSettings: ViewModelSettings = viewModel<ViewModelSettings>()
) {
	val navController = rememberNavController()

	NavHost(
		navController = navController,
		startDestination = Screen.Home.route
	) {
		composable(Screen.Home.route) {
			ComposableHomeScreen(
				onNavigateToPicker = {
					navController.navigate(Screen.Picker.route)
				},
				onNavigateToSettings = {
					navController.navigate(Screen.Settings.route)
				}
			)
		}
		composable(Screen.Picker.route) {
			ComposablePickerScreen(
				onBackClick = {
					navController.popBackStack()
				}
			)
		}
		composable(Screen.Settings.route) {
			ComposableSettingsScreen(
				viewModel = viewModelSettings,
				onBackClick = {
					navController.popBackStack()
				}
			)
		}
	}
}
