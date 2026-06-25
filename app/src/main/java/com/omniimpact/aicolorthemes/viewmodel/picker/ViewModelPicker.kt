package com.omniimpact.aicolorthemes.viewmodel.picker

import com.omniimpact.aicolorthemes.utility.ClassLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omniimpact.aicolorthemes.model.ModelSingleColor
import com.omniimpact.aicolorthemes.utility.IDeepSeekResult
import com.omniimpact.aicolorthemes.repository.ThemeRepository
import com.omniimpact.aicolorthemes.utility.UtilitySettings
import com.omniimpact.aicolorthemes.ui.composable.home.IComposableThemeCreationRow
import androidx.core.graphics.toColorInt
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch


interface IViewModelPicker {
	val colorSelected: StateFlow<Color>
	val isColorActive: StateFlow<Boolean>
	fun updateColor(color: Color, shouldActivate: Boolean = true)
	fun toggleColorActive(active: Boolean)
	fun clearColor()

	val text: StateFlow<String>
	fun updateText(newValue: String)
	val placeholderText: String
	val buttonText: String
	fun onButtonClick()
	val themeCreationRowState: IComposableThemeCreationRow
	val isLoading: StateFlow<Boolean>
}

@HiltViewModel
class ViewModelPicker @Inject constructor(
	private val utilitySettings: UtilitySettings,
	private val themeRepository: ThemeRepository
) : ViewModel(), IViewModelPicker {
	private val _isLoading = MutableStateFlow(false)
	override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

	private val _colorSelected = MutableStateFlow(
		Color(utilitySettings.getInt("picker_color", Color.White.toArgb()))
	)
	override val colorSelected: StateFlow<Color> = _colorSelected.asStateFlow()

	private val _isColorActive = MutableStateFlow(
		utilitySettings.getBoolean("picker_color_active", false)
	)
	override val isColorActive: StateFlow<Boolean> = _isColorActive.asStateFlow()

	override fun updateColor(color: Color, shouldActivate: Boolean) {
		_colorSelected.value = color
		utilitySettings.saveInt("picker_color", color.toArgb())
		if (shouldActivate) {
			toggleColorActive(true)
		}
	}

	override fun toggleColorActive(active: Boolean) {
		_isColorActive.value = active
		utilitySettings.saveBoolean("picker_color_active", active)
	}

	override fun clearColor() {
		_colorSelected.value = Color.White
		utilitySettings.saveInt("picker_color", Color.White.toArgb())
		toggleColorActive(false)
	}

	private val _text = MutableStateFlow("")
	override val text: StateFlow<String> = _text.asStateFlow()

	override fun updateText(newValue: String) {
		_text.value = newValue
	}

	override val placeholderText = "Describe a Color"
	override val buttonText = "Pick"
	
	override fun onButtonClick() {
		ClassLog.d(ViewModelPicker::class, "onButtonClick triggered")
		val query = ModelSingleColor(promptQuery = _text.value, colorHex = "")
		viewModelScope.launch {
			themeRepository.getSingleColor(query).collect { result ->
				when (result) {
					is IDeepSeekResult.Loading -> {
						_isLoading.value = true
					}
					is IDeepSeekResult.Success -> {
						_isLoading.value = false
						val colorInHex = result.data.colorHex
						ClassLog.d(ViewModelPicker::class, "Received Hex Code: $colorInHex")
						val formattedHex = if (colorInHex.startsWith("#")) colorInHex else "#$colorInHex"
						try {
							val color = formattedHex.toColorInt()
							updateColor(Color(color))
						} catch (_: IllegalArgumentException) {
							ClassLog.e(ViewModelPicker::class, "Invalid hex code: $colorInHex")
						}
					}
					is IDeepSeekResult.Failure -> {
						_isLoading.value = false
					}
				}
			}
		}
	}

	override val themeCreationRowState: IComposableThemeCreationRow
		get() = object : IComposableThemeCreationRow {
			override val onPickerClick = { toggleColorActive(!_isColorActive.value) }
			override val pickerColor = if (_isColorActive.value) _colorSelected.value else Color.Transparent
			override val isSwatchActive = _isColorActive.value
			override val text = _text.value
			override val onTextChange = { newText: String -> updateText(newText) }
			override val placeholderText = this@ViewModelPicker.placeholderText
			override val buttonText = this@ViewModelPicker.buttonText
			override val isButtonActive = !_isLoading.value
			override val onButtonClick = { this@ViewModelPicker.onButtonClick() }
		}
}
