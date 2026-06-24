package com.omniimpact.aicolorthemes.ui.composable.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ComposableThemeCreationRow(
	modifier: Modifier = Modifier,
	onPickerClick: () -> Unit = {}
) {
	var text by remember { mutableStateOf("") }

	Row(
		modifier = modifier
			.fillMaxWidth()
			.padding(16.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		// First square button: outline and transparent fill
		OutlinedButton(
			onClick = onPickerClick,
			modifier = Modifier
				.height(56.dp)
				.aspectRatio(1f),
			shape = RectangleShape
		) {
			// Empty or simple indicator
		}

		// Text box
		OutlinedTextField(
			value = text,
			onValueChange = { text = it },
			modifier = Modifier
				.weight(1f)
				.padding(horizontal = 8.dp),
			placeholder = { Text("Enter theme description") }
		)

		Button(
			onClick = { /* TODO */ },
			modifier = Modifier
				.height(56.dp)
				.aspectRatio(1f),
			shape = RectangleShape,
			contentPadding = PaddingValues(0.dp)
		) {
			Text(
				text = "Create",
				style = MaterialTheme.typography.labelSmall,
				maxLines = 1
			)
		}
	}
}

@Preview(showBackground = true)
@Composable
fun ComposableThemeCreationRowPreview() {
	ComposableThemeCreationRow()
}
