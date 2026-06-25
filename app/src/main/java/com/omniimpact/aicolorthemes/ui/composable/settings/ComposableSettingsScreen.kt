package com.omniimpact.aicolorthemes.ui.composable.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.omniimpact.aicolorthemes.model.ThemeModel
import com.omniimpact.aicolorthemes.ui.composable.app.ComposableAppScaffold
import com.omniimpact.aicolorthemes.ui.composable.app.Screen
import com.omniimpact.aicolorthemes.ui.theme.AIColorThemesTheme
import com.omniimpact.aicolorthemes.viewmodel.settings.IViewModelSettings
import com.omniimpact.aicolorthemes.viewmodel.settings.ViewModelSettings
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun ComposableSettingsScreen(
	viewModel: IViewModelSettings = hiltViewModel<ViewModelSettings>(),
	onBackClick: () -> Unit,
) {
	val apiKey by viewModel.apiKey.collectAsStateWithLifecycle()
	val themeSelected by viewModel.themeSelected.collectAsStateWithLifecycle()

	val apiKeySetting = remember(apiKey) {
		object : IComposableTextSetting by viewModel.apiKeySetting {
			override val value: String get() = apiKey
		}
	}

	val themeSetting = remember(themeSelected) {
		object : IComposableDropdownSetting by viewModel.themeSetting {
			override val selectedOption: String get() = themeSelected.name
		}
	}

	ComposableAppScaffold(
		title = Screen.Settings.title,
		onBackClick = onBackClick,
	) { innerPadding ->
		Column(modifier = Modifier.padding(innerPadding)) {
			ComposableTextSetting(setting = apiKeySetting)
			ComposableDropdownSetting(setting = themeSetting)
		}
	}
}

class MockViewModelSettings(
	theme: ThemeModel,
	key: String = ""
) : IViewModelSettings {
	override val themes = listOf(
		ThemeModel("Light", "light"),
		ThemeModel("Dark", "dark")
	)
	override val themeSelected = MutableStateFlow(theme)
	override fun selectTheme(theme: ThemeModel) { themeSelected.value = theme }
	
	override val apiKey = MutableStateFlow(key)
	override fun updateApiKey(newValue: String) { apiKey.value = newValue }

	override val apiKeySetting = object : IComposableTextSetting {
		override val name = "API Key"
		override val placeholder = "Enter your API key"
		override val key = "api_key"
		override val value: String get() = apiKey.value
		override val onValueChange: (String) -> Unit = { updateApiKey(it) }
	}

	override val themeSetting = object : IComposableDropdownSetting {
		override val name = "Theme"
		override val selectedOption: String get() = themeSelected.value.name
		override val options: List<String> get() = themes.map { it.name }
		override val onOptionSelected: (String) -> Unit = { name ->
			themes.find { it.name == name }?.let { selectTheme(it) }
		}
	}
}

@Preview(showBackground = true, name = "Light Theme")
@Composable
fun PreviewSettingsScreenLight() {
	AIColorThemesTheme(darkTheme = false) {
		ComposableSettingsScreen(
			viewModel = MockViewModelSettings(ThemeModel("Light", "light")),
		) {}
	}
}

@Preview(showBackground = true, name = "Dark Theme")
@Composable
fun PreviewSettingsScreenDark() {
	AIColorThemesTheme(darkTheme = true) {
		ComposableSettingsScreen(
			viewModel = MockViewModelSettings(ThemeModel("Dark", "dark")),
		) {}
	}
}
