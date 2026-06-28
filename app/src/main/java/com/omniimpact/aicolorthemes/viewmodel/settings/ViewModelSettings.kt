package com.omniimpact.aicolorthemes.viewmodel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omniimpact.aicolorthemes.model.ThemeModel
import com.omniimpact.aicolorthemes.utility.IUtilitySettings
import com.omniimpact.aicolorthemes.ui.composable.settings.IComposableTextSetting
import com.omniimpact.aicolorthemes.ui.composable.settings.IComposableDropdownSetting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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
	private val utilitySettings: IUtilitySettings
) : ViewModel(), IViewModelSettings {

	override val themes = listOf(
		ThemeModel("Light", "light"),
		ThemeModel("Dark", "dark"),
		ThemeModel("Material 3 Light", "m3_light"),
		ThemeModel("Material 3 Dark", "m3_dark"),
		ThemeModel("Dynamic Light", "dynamic_light"),
		ThemeModel("Dynamic Dark", "dynamic_dark")
	)

	override val themeSelected: StateFlow<ThemeModel> = utilitySettings.getStringFlow("theme_key", "light")
		.map { key -> themes.find { it.key == key } ?: themes[0] }
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = themes[0]
		)

	override fun selectTheme(theme: ThemeModel) {
		viewModelScope.launch {
			utilitySettings.saveString("theme_key", theme.key)
		}
	}

	override val apiKey: StateFlow<String> = utilitySettings.getStringFlow("api_key", "")
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = ""
		)

	override fun updateApiKey(newValue: String) {
		viewModelScope.launch {
			utilitySettings.saveString("api_key", newValue)
		}
	}

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

	// Helper for checking if dark theme is selected (for the theme wrapper)
	val isDarkTheme: Boolean
		get() = themeSelected.value.key == "dark" || themeSelected.value.key == "m3_dark" || themeSelected.value.key == "dynamic_dark"
}
