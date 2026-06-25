package com.omniimpact.aicolorthemes.viewmodel.home

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import com.omniimpact.aicolorthemes.model.ModelColorTheme
import com.omniimpact.aicolorthemes.utility.IDeepSeekResult
import com.omniimpact.aicolorthemes.utility.UtilityDeepSeekQuery
import com.omniimpact.aicolorthemes.ui.composable.home.IComposableThemeCreationRow
import com.omniimpact.aicolorthemes.utility.UtilitySettings
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.core.graphics.toColorInt

interface IViewModelHome {
	val colorSelected: StateFlow<Color>
	val isColorActive: StateFlow<Boolean>
	fun refreshSettings()
	val text: StateFlow<String>
	fun updateText(newValue: String)
	val themeCreationRowState: IComposableThemeCreationRow
	val themes: StateFlow<List<ModelColorTheme>>
	val isLoading: StateFlow<Boolean>
	fun removeTheme(theme: ModelColorTheme)
	fun clearThemes()
}

@HiltViewModel
class ViewModelHome @Inject constructor(
	private val utilitySettings: UtilitySettings,
	private val utilityDeepSeekQuery: UtilityDeepSeekQuery
) : ViewModel(), IViewModelHome {

	private val _colorSelected = MutableStateFlow(Color(utilitySettings.getInt("picker_color", Color.Transparent.toArgb())))
	override val colorSelected: StateFlow<Color> = _colorSelected.asStateFlow()

	private val _isColorActive = MutableStateFlow(utilitySettings.getBoolean("picker_color_active", false))
	override val isColorActive: StateFlow<Boolean> = _isColorActive.asStateFlow()

	override fun refreshSettings() {
		_colorSelected.value = Color(utilitySettings.getInt("picker_color", Color.Transparent.toArgb()))
		_isColorActive.value = utilitySettings.getBoolean("picker_color_active", false)
	}

	private val _text = MutableStateFlow("")
	override val text: StateFlow<String> = _text.asStateFlow()
	
	private val _themes = MutableStateFlow(listOf<ModelColorTheme>())
	override val themes: StateFlow<List<ModelColorTheme>> = _themes.asStateFlow()

	private val _isLoading = MutableStateFlow(false)
	override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

	override fun updateText(newValue: String) {
		_text.value = newValue
	}

	override fun removeTheme(theme: ModelColorTheme) {
		_themes.update { it.filter { t -> t != theme } }
	}

	override fun clearThemes() {
		_themes.value = emptyList()
	}
	
	private fun createTheme() {
		val anchorColor = if (_isColorActive.value) {
			"Anchor Color: #${Integer.toHexString(_colorSelected.value.toArgb()).substring(2)} \n\n"
		} else ""
		
		val query = ModelColorTheme(
			promptQuery = anchorColor + _text.value,
			themeName = "",
			themeDescription = "",
			colorTheme = emptyList()
		)
		
		_isLoading.value = true
		utilityDeepSeekQuery.send(query, object : IDeepSeekResult<ModelColorTheme> {
			override fun onSuccess(result: ModelColorTheme) {
				_isLoading.value = false
				val validColors = result.colorTheme.filter { colorHex ->
					try {
                        colorHex.toColorInt()
						true
					} catch (_: Exception) {
						false
					}
				}
				_themes.update { listOf(result.copy(colorTheme = validColors)) + it }
			}
			override fun onFailure(message: String) {
				_isLoading.value = false
			}
		})
	}

	override val themeCreationRowState: IComposableThemeCreationRow
		get() = object : IComposableThemeCreationRow {
			override val onPickerClick = {} 
			override val pickerColor: Color
				get() = if (_isColorActive.value) _colorSelected.value else Color.Transparent
			override val isSwatchActive: Boolean
				get() = _isColorActive.value
			override val text: String
				get() = _text.value
			override val onTextChange = { newText: String -> updateText(newText) }
			override val placeholderText = "Enter theme description"
			override val buttonText = "Create"
			override val isButtonActive: Boolean
				get() = !_isLoading.value
			override val onButtonClick = { createTheme() }
		}
}
