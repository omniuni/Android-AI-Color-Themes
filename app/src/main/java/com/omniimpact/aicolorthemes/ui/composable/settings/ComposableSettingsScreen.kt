package com.omniimpact.aicolorthemes.ui.composable.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.omniimpact.aicolorthemes.model.ThemeModel
import com.omniimpact.aicolorthemes.ui.composable.app.ComposableAppScaffold
import com.omniimpact.aicolorthemes.ui.theme.AIColorThemesTheme
import com.omniimpact.aicolorthemes.viewmodel.settings.IViewModelSettings
import com.omniimpact.aicolorthemes.viewmodel.settings.ViewModelSettings

@Composable
fun ComposableSettingsScreen(
	viewModel: IViewModelSettings = viewModel<ViewModelSettings>(),
	onBackClick: () -> Unit
) {
	ComposableAppScaffold(
		title = com.omniimpact.aicolorthemes.ui.composable.app.Screen.Settings.title,
		onBackClick = onBackClick
	) { innerPadding ->
		Column(modifier = Modifier.padding(innerPadding)) {
			ComposableTextSetting(setting = viewModel.apiKeySetting)
			ComposableDropdownSetting(setting = viewModel.themeSetting)
		}
	}
}

class MockViewModelSettings(
	override val themeSelected: ThemeModel,
	override val apiKey: String = ""
) : IViewModelSettings {
	override val themes = listOf(
		ThemeModel("Light", "light"),
		ThemeModel("Dark", "dark")
	)
	override fun selectTheme(theme: ThemeModel) {}
	override fun updateApiKey(newValue: String) {}

	override val apiKeySetting = object : IComposableTextSetting {
		override val name = "API Key"
		override val placeholder = "Enter your API key"
		override val key = "api_key"
		override val value = apiKey
		override val onValueChange: (String) -> Unit = {}
	}

	override val themeSetting = object : IComposableDropdownSetting {
		override val name = "Theme"
		override val selectedOption = themeSelected.name
		override val options = themes.map { it.name }
		override val onOptionSelected: (String) -> Unit = {}
	}
}

@Preview(showBackground = true, name = "Light Theme")
@Composable
fun PreviewSettingsScreenLight() {
	AIColorThemesTheme(darkTheme = false) {
		ComposableSettingsScreen(
			viewModel = MockViewModelSettings(ThemeModel("Light", "light")),
			onBackClick = {}
		)
	}
}

@Preview(showBackground = true, name = "Dark Theme")
@Composable
fun PreviewSettingsScreenDark() {
	AIColorThemesTheme(darkTheme = true) {
		ComposableSettingsScreen(
			viewModel = MockViewModelSettings(ThemeModel("Dark", "dark")),
			onBackClick = {}
		)
	}
}
