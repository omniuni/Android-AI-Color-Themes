package com.omniimpact.aicolorthemes.ui.composable.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.omniimpact.aicolorthemes.ui.composable.app.ComposableAppScaffold

@Composable
fun ComposableHomeScreen(
	onNavigateToPicker: () -> Unit,
	onNavigateToSettings: () -> Unit
) {
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
				onPickerClick = onNavigateToPicker
			)
		}
	}
}
