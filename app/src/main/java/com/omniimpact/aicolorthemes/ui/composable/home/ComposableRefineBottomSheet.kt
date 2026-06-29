package com.omniimpact.aicolorthemes.ui.composable.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.omniimpact.aicolorthemes.R
import com.omniimpact.aicolorthemes.model.ModelColorTheme
import com.omniimpact.aicolorthemes.ui.theme.AIColorThemesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposableRefineBottomSheet(
	theme: ModelColorTheme,
	onDismiss: () -> Unit,
	onSubmit: (ModelColorTheme, String) -> Unit
) {
	val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
	var requestText by remember { mutableStateOf("") }

	ModalBottomSheet(
		onDismissRequest = onDismiss,
		sheetState = sheetState
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp)
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = stringResource(R.string.refine_theme),
					style = MaterialTheme.typography.titleLarge
				)
				IconButton(onClick = onDismiss) {
					Icon(
						imageVector = Icons.Default.Close,
						contentDescription = stringResource(R.string.close)
					)
				}
			}

			ComposableThemeItem(
				theme = theme,
				showRefineButton = false,
				showRemoveButton = false
			)

			OutlinedTextField(
				value = requestText,
				onValueChange = { requestText = it },
				placeholder = { Text("Describe desired changes...") },
				modifier = Modifier.fillMaxWidth(),
				singleLine = false,
				maxLines = 3
			)

			Button(
				onClick = {
					onSubmit(theme, requestText)
					onDismiss()
				},
				modifier = Modifier.fillMaxWidth(),
				enabled = requestText.isNotBlank()
			) {
				Text(text = stringResource(R.string.create))
			}
		}
	}
}

@PreviewLightDark
@Composable
fun PreviewRefineBottomSheet() {
	AIColorThemesTheme {
		ComposableRefineBottomSheet(
			theme = ModelColorTheme(
				promptQuery = "",
				themeName = "Sample Theme",
				themeDescription = "This is a sample theme description.",
				colorTheme = listOf("#FF0000", "#00FF00", "#0000FF")
			),
			onDismiss = {},
			onSubmit = { _: ModelColorTheme, _: String -> }
		)
	}
}