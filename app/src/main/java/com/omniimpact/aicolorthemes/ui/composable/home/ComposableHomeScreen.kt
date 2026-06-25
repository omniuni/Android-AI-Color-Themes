package com.omniimpact.aicolorthemes.ui.composable.home

import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.omniimpact.aicolorthemes.model.ModelColorTheme
import com.omniimpact.aicolorthemes.ui.composable.app.ComposableAppScaffold
import com.omniimpact.aicolorthemes.ui.composable.app.Screen.Home
import com.omniimpact.aicolorthemes.ui.theme.AIColorThemesTheme
import com.omniimpact.aicolorthemes.viewmodel.home.IViewModelHome
import com.omniimpact.aicolorthemes.viewmodel.home.ViewModelHome
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun ComposableHomeScreen(
	onNavigateToPicker: () -> Unit,
	onNavigateToSettings: () -> Unit,
	viewModel: IViewModelHome = hiltViewModel<ViewModelHome>()
) {
	val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
	val themes by viewModel.themes.collectAsStateWithLifecycle()
	val colorSelected by viewModel.colorSelected.collectAsStateWithLifecycle()
	val isColorActive by viewModel.isColorActive.collectAsStateWithLifecycle()
	val text by viewModel.text.collectAsStateWithLifecycle()

	LaunchedEffect(Unit) {
		viewModel.refreshSettings()
	}
	ComposableAppScaffold(
		title = Home.title,
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
				val baseState = viewModel.themeCreationRowState
				val creationState = remember(baseState, isLoading, onNavigateToPicker, colorSelected, isColorActive, text) {
					object : IComposableThemeCreationRow by baseState {
						override val onPickerClick = onNavigateToPicker
						override val pickerColor = colorSelected
						override val isSwatchActive = isColorActive
						override val text = text
						override val onButtonClick = {
							if (!isLoading) baseState.onButtonClick()
						}
					}
				}
				val isEmpty = themes.isEmpty()
				androidx.compose.animation.AnimatedContent(
					targetState = isEmpty,
					label = "HomeContentAnimation",
					modifier = Modifier.fillMaxSize(),
					transitionSpec = {
						fadeIn() + expandVertically() togetherWith fadeOut() + shrinkVertically()
					}
				) { isListEmpty ->
					Column(
						modifier = Modifier
							.fillMaxSize()
							.then(if (isListEmpty) Modifier else Modifier.padding(bottom = 8.dp)),
						verticalArrangement = if (isListEmpty) Arrangement.Center else Arrangement.Top
					) {
						ComposableThemeCreationRow(
							state = creationState,
							modifier = Modifier.padding(8.dp)
						)
						if (isListEmpty) {
                            Text(
                                text = "To create a color theme,\n provide an anchor color and description\n and click Create.",
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
						} else {
							LazyColumn {
								items(themes) { theme ->
									ComposableThemeItem(theme = theme, onRemove = { viewModel.removeTheme(theme) })
								}
							}
						}
					}
				}
			}
			if (isLoading) {
				androidx.compose.material3.Card(
					modifier = Modifier.align(Alignment.Center).padding(16.dp),
					elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
				) {
					Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
						CircularProgressIndicator()
                        Text(
                            text = "Creating Your Theme",
                            modifier = Modifier.padding(top = 8.dp)
                        )
					}
				}
			}
		}
	}
}

class MockViewModelHome : IViewModelHome {
	override val colorSelected = MutableStateFlow(Color.Red)
	override val isColorActive = MutableStateFlow(true)
	override fun refreshSettings() {}
	override val text = MutableStateFlow("")
	override fun updateText(newValue: String) {}
	override val themeCreationRowState = object : IComposableThemeCreationRow {
		override val onPickerClick = {}
		override val pickerColor = Color.Red
		override val isSwatchActive = true
		override val text = ""
		override val onTextChange: (String) -> Unit = {}
		override val placeholderText = "Theme name"
		override val buttonText = "Generate"
		override val onButtonClick = {}
		override val isButtonActive = true
	}
	override val themes = MutableStateFlow(listOf<ModelColorTheme>())
	override val isLoading = MutableStateFlow(false)
	override fun removeTheme(theme: ModelColorTheme) {}
	override fun clearThemes() {}
}

@PreviewLightDark
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
