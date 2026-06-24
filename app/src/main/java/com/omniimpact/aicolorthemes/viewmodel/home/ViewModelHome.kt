package com.omniimpact.aicolorthemes.viewmodel.home

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.AndroidViewModel
import com.omniimpact.aicolorthemes.model.ModelColorTheme
import com.omniimpact.aicolorthemes.utility.IDeepSeekResult
import com.omniimpact.aicolorthemes.utility.UtilityDeepSeekQuery
import com.omniimpact.aicolorthemes.ui.composable.home.IComposableThemeCreationRow
import com.omniimpact.aicolorthemes.utility.UtilitySettings

interface IViewModelHome {
	val colorSelected: Color
	val isColorActive: Boolean
	fun refreshSettings()
	val text: String
	fun updateText(newValue: String)
	val themeCreationRowState: IComposableThemeCreationRow
	val themes: List<ModelColorTheme>
	val isLoading: Boolean
	fun removeTheme(theme: ModelColorTheme)
	fun clearThemes()
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
	
	override var themes by mutableStateOf(listOf<ModelColorTheme>())
		private set

	override var isLoading by mutableStateOf(false)
		private set

	override fun updateText(newValue: String) {
		text = newValue
	}

	override fun removeTheme(theme: ModelColorTheme) {
		themes = themes.filter { it != theme }
	}

	override fun clearThemes() {
		themes = emptyList()
	}
	
	private fun createTheme() {
		val anchorColor = if (isColorActive) {
			"Anchor Color: #${Integer.toHexString(colorSelected.toArgb()).substring(2)} \n\n"
		} else ""
		
		val query = ModelColorTheme(
			promptQuery = anchorColor + text,
			themeName = "",
			themeDescription = "",
			colorTheme = emptyList()
		)
		
		isLoading = true
		UtilityDeepSeekQuery.send(getApplication(), query, object : IDeepSeekResult<ModelColorTheme> {
			override fun onSuccess(result: ModelColorTheme) {
				isLoading = false
				val validColors = result.colorTheme.filter { colorHex ->
					try {
						android.graphics.Color.parseColor(colorHex)
						true
					} catch (_: Exception) {
						false
					}
				}
				themes = listOf(result.copy(colorTheme = validColors)) + themes
			}
			override fun onFailure(message: String) {
				isLoading = false
			}
		})
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
			override val onButtonClick = { createTheme() }
		}
}
