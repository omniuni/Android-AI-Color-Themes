package com.omniimpact.aicolorthemes.ui.composable.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.omniimpact.aicolorthemes.ui.composable.app.ComposableAppScaffold

import androidx.lifecycle.viewmodel.compose.viewModel
import com.omniimpact.aicolorthemes.viewmodel.home.ViewModelHome

@Composable
fun ComposableHomeScreen(
	onNavigateToPicker: () -> Unit,
	onNavigateToSettings: () -> Unit,
	viewModel: ViewModelHome = viewModel()
) {
	LaunchedEffect(Unit) {
		viewModel.refreshSettings()
	}
	LaunchedEffect(Unit) {
		viewModel.refreshSettings()
	}
	ComposableAppScaffold(
		title = "AI Color Themes",
		actions = {
			IconButton(onClick = onNavigateToSettings) {
				Icon(
					imageVector = Icons.Default.Settings,
					contentDescription = "Settings"
				)
			}
		}
	) { innerPadding ->
		Column(modifier = Modifier.padding(innerPadding)) {
			ComposableThemeCreationRow(
				state = viewModel.themeCreationRowState.let { baseState ->
					object : IComposableThemeCreationRow by baseState {
						override val onPickerClick = {
							// For now, keep the navigation but don't toggle automatically
							// The reverting issue might be elsewhere
							onNavigateToPicker()
						}
					}
				}
			)
		}
	}
}
