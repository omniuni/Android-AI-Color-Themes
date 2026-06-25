package com.omniimpact.aicolorthemes.viewmodel.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.omniimpact.aicolorthemes.model.ThemeModel
import com.omniimpact.aicolorthemes.utility.UtilitySettings
import com.omniimpact.aicolorthemes.ui.composable.settings.IComposableTextSetting
import com.omniimpact.aicolorthemes.ui.composable.settings.IComposableDropdownSetting
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

interface IViewModelSettings {
	val themes: List<ThemeModel>
	val themeSelected: ThemeModel
	fun selectTheme(theme: ThemeModel)
	val apiKey: String
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

	override var themeSelected by mutableStateOf(
		themes.find { it.key == utilitySettings.getString("theme_key", "light") } ?: themes[0]
	)
		private set

	override fun selectTheme(theme: ThemeModel) {
		themeSelected = theme
		utilitySettings.saveString("theme_key", theme.key)
	}

	override var apiKey by mutableStateOf(utilitySettings.getString("api_key", ""))
		private set

	override fun updateApiKey(newValue: String) {
		apiKey = newValue
		utilitySettings.saveString("api_key", newValue)
	}

	override val apiKeySetting = object : IComposableTextSetting {
		override val name = "API Key"
		override val placeholder = "Enter your API key"
		override val key = "api_key"
		override val value: String get() = apiKey
		override val onValueChange: (String) -> Unit = { updateApiKey(it) }
	}

	override val themeSetting = object : IComposableDropdownSetting {
		override val name = "Theme"
		override val selectedOption: String get() = themeSelected.name
		override val options: List<String> get() = themes.map { it.name }
		override val onOptionSelected: (String) -> Unit = { name ->
			themes.find { it.name == name }?.let { selectTheme(it) }
		}
	}

	// Helper for checking if dark theme is selected (for the theme wrapper)
	val isDarkTheme: Boolean
		get() = themeSelected.key == "dark"
}
