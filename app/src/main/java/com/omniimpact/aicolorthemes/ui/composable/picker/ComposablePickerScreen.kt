package com.omniimpact.aicolorthemes.ui.composable.picker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.omniimpact.aicolorthemes.ui.composable.app.ComposableAppScaffold
import com.omniimpact.aicolorthemes.ui.composable.home.ComposableThemeCreationRow
import com.omniimpact.aicolorthemes.ui.composable.home.IComposableThemeCreationRow
import com.omniimpact.aicolorthemes.viewmodel.picker.IViewModelPicker
import com.omniimpact.aicolorthemes.ui.theme.AIColorThemesTheme
import com.omniimpact.aicolorthemes.viewmodel.picker.ViewModelPicker
import com.omniimpact.aicolorthemes.ui.composable.app.Screen.Picker
import androidx.compose.ui.res.stringResource
import com.omniimpact.aicolorthemes.R
import androidx.compose.foundation.layout.Spacer
import com.github.skydoves.colorpicker.compose.*

@Composable
fun ComposablePickerScreen(
	viewModel: IViewModelPicker = hiltViewModel<ViewModelPicker>(),
	onBackClick: () -> Unit,
) {

	val colorSelected by viewModel.colorSelected.collectAsStateWithLifecycle()
	val isColorActive by viewModel.isColorActive.collectAsStateWithLifecycle()
	val text by viewModel.text.collectAsStateWithLifecycle()
	val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

	val controller = rememberColorPickerController()
	androidx.compose.runtime.LaunchedEffect(colorSelected) {
		controller.selectByColor(colorSelected, fromUser = false)
	}

	val baseState = viewModel.themeCreationRowState
	val creationState = remember(baseState, isLoading, colorSelected, isColorActive, text) {
		object : IComposableThemeCreationRow by baseState {
			override val pickerColor = if (isColorActive) colorSelected else Color.Transparent
			override val isSwatchActive = isColorActive
			override val text = text
			override val isButtonActive = !isLoading
		}
	}

	ComposableAppScaffold(
		title = Picker.title,
		onBackClick = onBackClick,
		actions = {
			IconButton(
				onClick = { viewModel.clearColor() }
			) {
				Icon(
					imageVector = Icons.Default.Delete,
					contentDescription = stringResource(R.string.clear_color)
				)
			}
		}
	) { innerPadding ->
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding)
		) {
			Column(
				modifier = Modifier
					.fillMaxSize()
			) {
				Column(
					modifier = Modifier
						.fillMaxWidth()
						.weight(1f)
						.verticalScroll(rememberScrollState()),
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.Center
				) {
					Box(
						modifier = Modifier
							.fillMaxWidth()
							.padding(16.dp),
						contentAlignment = Alignment.Center
					) {
						Column(horizontalAlignment = Alignment.CenterHorizontally) {
							HsvColorPicker(
								modifier = Modifier
									.fillMaxWidth()
									.height(300.dp),
								controller = controller,
								onColorChanged = { colorEnvelope ->
									if (colorEnvelope.fromUser) {
										viewModel.updateColor(colorEnvelope.color, shouldActivate = true)
									}
								},
								initialColor = colorSelected
							)

							Spacer(modifier = Modifier.height(16.dp))

							BrightnessSlider(
								modifier = Modifier
									.fillMaxWidth()
									.height(40.dp),
								controller = controller
							)
						}
					}
				}
				ComposableThemeCreationRow(
					state = creationState,
					modifier = Modifier.padding(8.dp)
				)
			}
			if (isLoading) {
				androidx.compose.material3.Card(
					modifier = Modifier.align(Alignment.Center).padding(16.dp),
					elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 8.dp)
				) {
					Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
						CircularProgressIndicator()
						androidx.compose.material3.Text(
							text = stringResource(R.string.picking_your_color),
							modifier = Modifier.padding(top = 8.dp)
						)
					}
				}
			}
		}
	}
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun ComposablePickerScreenPreviewLight() {
	AIColorThemesTheme(darkTheme = false) {
		ComposablePickerScreen(
			viewModel = object : IViewModelPicker {
				override val colorSelected = kotlinx.coroutines.flow.MutableStateFlow(Color.Red)
				override val isColorActive = kotlinx.coroutines.flow.MutableStateFlow(true)
				override fun updateColor(color: Color, shouldActivate: Boolean) {}
				override fun toggleColorActive(active: Boolean) {}
				override fun clearColor() {}
				override val text = kotlinx.coroutines.flow.MutableStateFlow("")
				override fun updateText(newValue: String) {}
				override val placeholderText = ""
				override val buttonText = ""
				override fun onButtonClick() {}
				override val themeCreationRowState = object : IComposableThemeCreationRow {
					override val onPickerClick = {}
					override val pickerColor = Color.Red
					override val isSwatchActive = true
					override val text = ""
					override val onTextChange = { _: String -> }
					override val placeholderText = "Describe a Color"
					override val buttonText = "Pick"
					override val onButtonClick = {}
					override val isButtonActive = true
				}
				override val isLoading = kotlinx.coroutines.flow.MutableStateFlow(false)
			},
		) {}
	}
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun ComposablePickerScreenPreviewDark() {
	AIColorThemesTheme(darkTheme = true) {
		ComposablePickerScreen(
			viewModel = object : IViewModelPicker {
				override val colorSelected = kotlinx.coroutines.flow.MutableStateFlow(Color.Red)
				override val isColorActive = kotlinx.coroutines.flow.MutableStateFlow(true)
				override fun updateColor(color: Color, shouldActivate: Boolean) {}
				override fun toggleColorActive(active: Boolean) {}
				override fun clearColor() {}
				override val text = kotlinx.coroutines.flow.MutableStateFlow("")
				override fun updateText(newValue: String) {}
				override val placeholderText = ""
				override val buttonText = ""
				override fun onButtonClick() {}
				override val themeCreationRowState = object : IComposableThemeCreationRow {
					override val onPickerClick = {}
					override val pickerColor = Color.Red
					override val isSwatchActive = true
					override val text = ""
					override val onTextChange = { _: String -> }
					override val placeholderText = "Describe a Color"
					override val buttonText = "Pick"
					override val onButtonClick = {}
					override val isButtonActive = false
				}
				override val isLoading = kotlinx.coroutines.flow.MutableStateFlow(true)
			},
		) {}
	}
}
