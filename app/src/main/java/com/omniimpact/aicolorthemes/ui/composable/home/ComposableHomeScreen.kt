package com.omniimpact.aicolorthemes.ui.composable.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.omniimpact.aicolorthemes.ui.composable.app.ComposableAppScaffold
import com.omniimpact.aicolorthemes.ui.theme.AIColorThemesTheme
import com.omniimpact.aicolorthemes.viewmodel.home.IViewModelHome
import com.omniimpact.aicolorthemes.viewmodel.home.ViewModelHome

@Composable
fun ComposableHomeScreen(
	onNavigateToPicker: () -> Unit,
	onNavigateToSettings: () -> Unit,
	viewModel: IViewModelHome = viewModel<ViewModelHome>()
) {
	LaunchedEffect(Unit) {
		viewModel.refreshSettings()
	}
	ComposableAppScaffold(
		title = com.omniimpact.aicolorthemes.ui.composable.app.Screen.Home.title,
		actions = {
			IconButton(onClick = { viewModel.clearThemes() }) {
				Icon(
					imageVector = Icons.Default.Delete,
					contentDescription = "Clear all themes"
				)
			}
			IconButton(onClick = onNavigateToSettings) {
				Icon(
					imageVector = Icons.Default.Settings,
					contentDescription = "Settings"
				)
			}
		}
	) { innerPadding ->
		Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
			Column {
				ComposableThemeCreationRow(
					state = viewModel.themeCreationRowState.let { baseState ->
						object : IComposableThemeCreationRow by baseState {
							override val onPickerClick = {
								onNavigateToPicker()
							}
						}
					}
				)
				LazyColumn {
					items(viewModel.themes.size) { index ->
						val theme = viewModel.themes[index]
						ComposableThemeItem(theme = theme, onRemove = { viewModel.removeTheme(theme) })
					}
				}
			}
			if (viewModel.isLoading) {
				CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
			}
		}
	}
}

class MockViewModelHome : IViewModelHome {
	override val colorSelected = androidx.compose.ui.graphics.Color.Red
	override val isColorActive = true
	override fun refreshSettings() {}
	override val text = ""
	override fun updateText(newValue: String) {}
	override val themeCreationRowState = object : IComposableThemeCreationRow {
		override val onPickerClick = {}
		override val pickerColor = androidx.compose.ui.graphics.Color.Red
		override val isSwatchActive = true
		override val text = ""
		override val onTextChange: (String) -> Unit = {}
		override val placeholderText = "Theme name"
		override val buttonText = "Generate"
		override val onButtonClick = {}
	}
	override val themes = listOf(
		com.omniimpact.aicolorthemes.model.ModelColorTheme(
			themeName = "Mock Theme",
			themeDescription = "Description",
			promptQuery = "Query",
			colorTheme = listOf("#FF0000", "#00FF00", "#0000FF")
		)
	)
	override val isLoading = false
	override fun removeTheme(theme: com.omniimpact.aicolorthemes.model.ModelColorTheme) {}
	override fun clearThemes() {}
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
	AIColorThemesTheme {
		ComposableHomeScreen(
			onNavigateToPicker = {},
			onNavigateToSettings = {},
			viewModel = MockViewModelHome()
		)
	}
}
