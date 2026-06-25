package com.omniimpact.aicolorthemes.viewmodel.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.ViewModel
import com.omniimpact.aicolorthemes.model.ThemeModel
import com.omniimpact.aicolorthemes.utility.UtilitySettings
import com.omniimpact.aicolorthemes.ui.composable.settings.IComposableTextSetting
import com.omniimpact.aicolorthemes.ui.composable.settings.IComposableDropdownSetting
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

interface IViewModelSettings {
	val themes: List<ThemeModel>
	val themeSelected: StateFlow<ThemeModel>
	fun selectTheme(theme: ThemeModel)
	val apiKey: StateFlow<String>
	fun updateApiKey(newValue: String)

	val apiKeySetting: IComposableTextSetting
	val themeSetting: IComposableDropdownSetting
}

@HiltViewModel
class ViewModelSettings @Inject constructor(
	private val utilitySettings: UtilitySettings
) : ViewModel(), IViewModelSettings {

	override val themes = listOf(
		ThemeModel("Light", "light"),
		ThemeModel("Dark", "dark")
	)

	private val _themeSelected = MutableStateFlow(
		themes.find { it.key == utilitySettings.getString("theme_key", "light") } ?: themes[0]
	)
	override val themeSelected: StateFlow<ThemeModel> = _themeSelected.asStateFlow()

	override fun selectTheme(theme: ThemeModel) {
		_themeSelected.value = theme
		utilitySettings.saveString("theme_key", theme.key)
	}

	private val _apiKey = MutableStateFlow(utilitySettings.getString("api_key", ""))
	override val apiKey: StateFlow<String> = _apiKey.asStateFlow()

	override fun updateApiKey(newValue: String) {
		_apiKey.value = newValue
		utilitySettings.saveString("api_key", newValue)
	}

	override val apiKeySetting = object : IComposableTextSetting {
		override val name = "API Key"
		override val placeholder = "Enter your API key"
		override val key = "api_key"
		override val value: String get() = _apiKey.value
		override val onValueChange: (String) -> Unit = { updateApiKey(it) }
	}

	override val themeSetting = object : IComposableDropdownSetting {
		override val name = "Theme"
		override val selectedOption: String get() = _themeSelected.value.name
		override val options: List<String> get() = themes.map { it.name }
		override val onOptionSelected: (String) -> Unit = { name ->
			themes.find { it.name == name }?.let { selectTheme(it) }
		}
	}

	// Helper for checking if dark theme is selected (for the theme wrapper)
	val isDarkTheme: Boolean
		get() = _themeSelected.value.key == "dark"
}
