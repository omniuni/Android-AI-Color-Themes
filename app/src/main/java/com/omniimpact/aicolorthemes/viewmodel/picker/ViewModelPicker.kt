package com.omniimpact.aicolorthemes.viewmodel.picker

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.AndroidViewModel
import com.omniimpact.aicolorthemes.utility.UtilitySettings
import com.omniimpact.aicolorthemes.ui.composable.home.IComposableThemeCreationRow

interface IViewModelPicker {
	val colorSelected: Color
	val isColorActive: Boolean
	fun updateColor(color: Color, shouldActivate: Boolean = true)
	fun toggleColorActive(active: Boolean)
	fun clearColor()

	val text: String
	fun updateText(newValue: String)
	val placeholderText: String
	val buttonText: String
	fun onButtonClick()
	val themeCreationRowState: IComposableThemeCreationRow
}

class ViewModelPicker(application: Application) : AndroidViewModel(application), IViewModelPicker {
	private val utilitySettings = UtilitySettings(application)

	override var colorSelected by mutableStateOf(
		Color(utilitySettings.getInt("picker_color", Color.White.toArgb()))
	)
		private set

	override var isColorActive by mutableStateOf(
		utilitySettings.getBoolean("picker_color_active", false)
	)
		private set

	override fun updateColor(color: Color, shouldActivate: Boolean) {
		colorSelected = color
		utilitySettings.saveInt("picker_color", color.toArgb())
		if (shouldActivate) {
			toggleColorActive(true)
		}
	}

	override fun toggleColorActive(active: Boolean) {
		isColorActive = active
		utilitySettings.saveBoolean("picker_color_active", active)
	}

	override fun clearColor() {
		colorSelected = Color.White
		utilitySettings.saveInt("picker_color", Color.White.toArgb())
		toggleColorActive(false)
	}

	override var text by mutableStateOf("")
		private set

	override fun updateText(newValue: String) {
		text = newValue
	}

	override val placeholderText = "Describe a Color"
	override val buttonText = "Pick"
	override fun onButtonClick() {}

	override val themeCreationRowState: IComposableThemeCreationRow
		get() = object : IComposableThemeCreationRow {
			override val onPickerClick = { toggleColorActive(!isColorActive) }
			override val pickerColor = if (isColorActive) colorSelected else Color.Transparent
			override val isSwatchActive = isColorActive
			override val text = this@ViewModelPicker.text
			override val onTextChange = { newText: String -> updateText(newText) }
			override val placeholderText = this@ViewModelPicker.placeholderText
			override val buttonText = this@ViewModelPicker.buttonText
			override val onButtonClick = { this@ViewModelPicker.onButtonClick() }
		}
}
