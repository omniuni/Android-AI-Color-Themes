package com.omniimpact.aicolorthemes.ui.composable.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omniimpact.aicolorthemes.R

interface IComposableThemeCreationRow {
	val onPickerClick: () -> Unit
	val pickerColor: Color
	val isSwatchActive: Boolean
	val text: String
	val onTextChange: (String) -> Unit
	val placeholderText: String
	val buttonText: String
	val isButtonActive: Boolean
	val onButtonClick: () -> Unit
}

@Composable
fun ComposableThemeCreationRow(
	state: IComposableThemeCreationRow,
	modifier: Modifier = Modifier
) {
	val focusManager = LocalFocusManager.current
	Card(
		modifier = modifier.fillMaxWidth(),
		shape = MaterialTheme.shapes.medium
	) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			verticalAlignment = Alignment.CenterVertically
		) {
			// Color Swatch Button
			Button(
				onClick = {
					focusManager.clearFocus()
					state.onPickerClick()
				},
				modifier = Modifier
					.height(56.dp)
					.aspectRatio(1f),
				shape = RectangleShape,
				contentPadding = PaddingValues(0.dp),
				colors = ButtonDefaults.buttonColors(
					containerColor = state.pickerColor,
					contentColor = MaterialTheme.colorScheme.onSurface
				)
			) {
				if (!state.isSwatchActive) {
					Icon(
						imageVector = Icons.Default.Close,
						contentDescription = stringResource(R.string.inactive)
					)
				}
			}

			// Prompt Text Box
			TextField(
				value = state.text,
				onValueChange = state.onTextChange,
				modifier = Modifier
					.weight(1f)
					.height(56.dp),
				placeholder = { Text(state.placeholderText) },
				maxLines = 1,
				singleLine = true,
				colors = TextFieldDefaults.colors(
					focusedIndicatorColor = Color.Transparent,
					unfocusedIndicatorColor = Color.Transparent,
					disabledIndicatorColor = Color.Transparent
				)
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
				contentPadding = PaddingValues(0.dp),
				enabled = state.isButtonActive
			) {
				Text(
					text = state.buttonText,
					style = MaterialTheme.typography.labelSmall,
					maxLines = 1
				)
			}
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
	override val isButtonActive = true
	override val onButtonClick = {}
}

@Preview
@Composable
fun ComposableThemeCreationRowPreview() {
	com.omniimpact.aicolorthemes.ui.theme.AIColorThemesTheme(darkTheme = false) {
		ComposableThemeCreationRow(state = mockState)
	}
}

@Preview
@Composable
fun ComposableThemeCreationRowPreviewDark() {
	com.omniimpact.aicolorthemes.ui.theme.AIColorThemesTheme(darkTheme = true) {
		ComposableThemeCreationRow(state = mockState)
	}
}
