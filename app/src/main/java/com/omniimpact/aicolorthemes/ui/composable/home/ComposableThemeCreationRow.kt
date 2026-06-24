package com.omniimpact.aicolorthemes.ui.composable.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

interface IComposableThemeCreationRow {
	val onPickerClick: () -> Unit
	val pickerColor: Color
	val isSwatchActive: Boolean
	val text: String
	val onTextChange: (String) -> Unit
	val placeholderText: String
	val buttonText: String
	val onButtonClick: () -> Unit
}

@Composable
fun ComposableThemeCreationRow(
	state: IComposableThemeCreationRow,
	modifier: Modifier = Modifier
) {
	val focusManager = LocalFocusManager.current
	Row(
		modifier = modifier
			.fillMaxWidth()
			.padding(16.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		// Color Swatch Button
		OutlinedButton(
			onClick = {
				focusManager.clearFocus()
				state.onPickerClick()
			},
			modifier = Modifier
				.height(56.dp)
				.aspectRatio(1f),
			shape = RectangleShape,
			colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
				containerColor = state.pickerColor
			),
			contentPadding = PaddingValues(0.dp)
		) {
			if (!state.isSwatchActive) {
				Icon(
					imageVector = Icons.Default.Close,
					contentDescription = "Inactive",
					tint = MaterialTheme.colorScheme.onSurface
				)
			}
		}

		// Prompt Text Box
		OutlinedTextField(
			value = state.text,
			onValueChange = state.onTextChange,
			modifier = Modifier
				.weight(1f)
				.padding(horizontal = 8.dp)
				.height(56.dp),
			placeholder = { Text(state.placeholderText) },
			maxLines = 1,
			singleLine = true
		)

		// Action Button
		Button(
			onClick = {
				focusManager.clearFocus()
				state.onButtonClick()
			},
			modifier = Modifier
				.height(56.dp)
				.aspectRatio(1f),
			shape = RectangleShape,
			contentPadding = PaddingValues(0.dp)
		) {
			Text(
				text = state.buttonText,
				style = MaterialTheme.typography.labelSmall,
				maxLines = 1
			)
		}
	}
}

val mockState = object : IComposableThemeCreationRow {
	override val onPickerClick = {}
	override val pickerColor = Color.Transparent
	override val isSwatchActive = true
	override val text = ""
	override val onTextChange = { _: String -> }
	override val placeholderText = "Enter theme description"
	override val buttonText = "Create"
	override val onButtonClick = {}
}

@Preview(showBackground = true)
@Composable
fun ComposableThemeCreationRowPreview() {
	com.omniimpact.aicolorthemes.ui.theme.AIColorThemesTheme(darkTheme = false) {
		ComposableThemeCreationRow(state = mockState)
	}
}
