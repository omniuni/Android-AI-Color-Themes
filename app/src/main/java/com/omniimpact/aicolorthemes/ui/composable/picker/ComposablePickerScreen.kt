package com.omniimpact.aicolorthemes.ui.composable.picker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.omniimpact.aicolorthemes.ui.composable.app.ComposableAppScaffold

@Composable
fun ComposablePickerScreen(
	onBackClick: () -> Unit
) {
	ComposableAppScaffold(
		title = "Picker",
		onBackClick = onBackClick
	) { innerPadding ->
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding),
			contentAlignment = Alignment.Center
		) {
			Text("Picker Screen")
		}
	}
}
