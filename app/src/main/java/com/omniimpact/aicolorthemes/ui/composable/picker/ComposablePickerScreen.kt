package com.omniimpact.aicolorthemes.ui.composable.picker

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.omniimpact.aicolorthemes.ui.composable.app.ComposableAppScaffold
import com.omniimpact.aicolorthemes.ui.composable.home.ComposableThemeCreationRow
import com.omniimpact.aicolorthemes.ui.composable.home.IComposableThemeCreationRow
import com.omniimpact.aicolorthemes.viewmodel.picker.IViewModelPicker
import com.omniimpact.aicolorthemes.ui.theme.AIColorThemesTheme

interface IViewModelPickerUI : IViewModelPicker {
	val isLoading: Boolean
}

@Composable
fun ComposablePickerScreen(
	viewModel: IViewModelPickerUI,
	onBackClick: () -> Unit
) {
	var shouldActivateColor = true
	val controller = rememberColorPickerController()

	androidx.compose.runtime.LaunchedEffect(viewModel.colorSelected) {
		shouldActivateColor = false
		controller.selectByColor(viewModel.colorSelected, false)
	}

	ComposableAppScaffold(
		title = "Picker",
		onBackClick = onBackClick,
		actions = {
			IconButton(onClick = {
				shouldActivateColor = false
				viewModel.clearColor()
				viewModel.toggleColorActive(false)
			}) {
				Icon(
					imageVector = Icons.Default.Delete,
					contentDescription = "Clear Color"
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
					.verticalScroll(rememberScrollState())
			) {
				Box(
					modifier = Modifier
						.fillMaxWidth()
						.weight(1f)
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
								viewModel.updateColor(colorEnvelope.color, shouldActivate = shouldActivateColor)
								if(!shouldActivateColor) shouldActivateColor = true
							},
							initialColor = viewModel.colorSelected
						)
						
						androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
						
						com.github.skydoves.colorpicker.compose.BrightnessSlider(
							modifier = Modifier
								.fillMaxWidth()
								.height(40.dp),
							controller = controller
						)
					}
				}
				ComposableThemeCreationRow(
					state = viewModel.themeCreationRowState
				)
			}
			if (viewModel.isLoading) {
				CircularProgressIndicator(
					modifier = Modifier.align(Alignment.Center)
				)
			}
		}
	}
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun ComposablePickerScreenPreviewLight() {
	AIColorThemesTheme(darkTheme = false) {
		ComposablePickerScreen(
			viewModel = object : IViewModelPickerUI {
				override val colorSelected = Color.Red
				override val isColorActive = true
				override fun updateColor(color: Color, shouldActivate: Boolean) {}
				override fun toggleColorActive(active: Boolean) {}
				override fun clearColor() {}
				override val text = ""
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
				}
				override val isLoading = false
			},
			onBackClick = {}
		)
	}
}
