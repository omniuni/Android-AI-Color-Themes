package com.omniimpact.aicolorthemes.viewmodel.home

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.AndroidViewModel
import com.omniimpact.aicolorthemes.ui.composable.home.IComposableThemeCreationRow
import com.omniimpact.aicolorthemes.utility.UtilitySettings

interface IViewModelHome {
	val colorSelected: Color
	val isColorActive: Boolean
	fun refreshSettings()
	val text: String
	fun updateText(newValue: String)
	val themeCreationRowState: IComposableThemeCreationRow
}

class ViewModelHome(application: Application) : AndroidViewModel(application), IViewModelHome {
	private val utilitySettings = UtilitySettings(application)

	override var colorSelected by mutableStateOf(Color(utilitySettings.getInt("picker_color", Color.Transparent.toArgb())))
		private set
	override var isColorActive by mutableStateOf(utilitySettings.getBoolean("picker_color_active", false))
		private set

	override fun refreshSettings() {
		colorSelected = Color(utilitySettings.getInt("picker_color", Color.Transparent.toArgb()))
		isColorActive = utilitySettings.getBoolean("picker_color_active", false)
	}

	override var text by mutableStateOf("")
		private set

	override fun updateText(newValue: String) {
		text = newValue
	}

	override val themeCreationRowState: IComposableThemeCreationRow
		get() = object : IComposableThemeCreationRow {
			override val onPickerClick = {} // Will be provided by navigation callback in view
			override val pickerColor = if (isColorActive) colorSelected else Color.Transparent
			override val isSwatchActive = isColorActive
			override val text = this@ViewModelHome.text
			override val onTextChange = { newText: String -> updateText(newText) }
			override val placeholderText = "Enter theme description"
			override val buttonText = "Create"
			override val onButtonClick = {}
		}
}
