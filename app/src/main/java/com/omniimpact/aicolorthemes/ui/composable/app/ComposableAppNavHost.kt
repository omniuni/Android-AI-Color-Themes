package com.omniimpact.aicolorthemes.ui.composable.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.omniimpact.aicolorthemes.viewmodel.home.ViewModelHome
import androidx.lifecycle.viewmodel.compose.viewModel
import com.omniimpact.aicolorthemes.ui.composable.home.ComposableHomeScreen
import com.omniimpact.aicolorthemes.ui.composable.picker.ComposablePickerScreen
import com.omniimpact.aicolorthemes.ui.composable.settings.ComposableSettingsScreen
import com.omniimpact.aicolorthemes.viewmodel.picker.ViewModelPicker
import com.omniimpact.aicolorthemes.viewmodel.settings.ViewModelSettings

@Composable
fun ComposableAppNavHost(
	viewModelSettings: ViewModelSettings,
	viewModelPicker: ViewModelPicker
) {
	val navController = rememberNavController()

	NavHost(
		navController = navController,
		startDestination = "home"
	) {
		composable("home") {
			ComposableHomeScreen(
				onNavigateToPicker = {
					navController.navigate("picker")
				},
				onNavigateToSettings = {
					navController.navigate("settings")
				}
			)
		}
		composable("picker") {
			ComposablePickerScreen(
				viewModel = viewModelPicker,
				onBackClick = {
					navController.popBackStack()
				}
			)
		}
		composable("settings") {
			ComposableSettingsScreen(
				viewModel = viewModelSettings,
				onBackClick = {
					navController.popBackStack()
				}
			)
		}
	}
}
