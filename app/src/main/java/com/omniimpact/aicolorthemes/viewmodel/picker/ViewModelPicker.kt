package com.omniimpact.aicolorthemes.viewmodel.picker

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColorInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omniimpact.aicolorthemes.model.ModelSingleColor
import com.omniimpact.aicolorthemes.repository.ThemeRepository
import com.omniimpact.aicolorthemes.ui.composable.home.IComposableThemeCreationRow
import com.omniimpact.aicolorthemes.utility.ClassLog
import com.omniimpact.aicolorthemes.utility.IDeepSeekResult
import com.omniimpact.aicolorthemes.utility.IUtilitySettings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

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
	private val utilitySettings: IUtilitySettings,
	private val themeRepository: ThemeRepository
) : ViewModel(), IViewModelPicker {
	private val _isLoading = MutableStateFlow(false)
	override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

	override val colorSelected: StateFlow<Color> = utilitySettings.getIntFlow("picker_color", Color.White.toArgb())
		.map { Color(it) }
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = Color.White
		)

	override val isColorActive: StateFlow<Boolean> = utilitySettings.getBooleanFlow("picker_color_active", false)
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = false
		)

	override fun updateColor(color: Color, shouldActivate: Boolean) {
		viewModelScope.launch {
			utilitySettings.saveInt("picker_color", color.toArgb())
			if (shouldActivate) {
				toggleColorActive(true)
			}
		}
	}

	override fun toggleColorActive(active: Boolean) {
		viewModelScope.launch {
			utilitySettings.saveBoolean("picker_color_active", active)
		}
	}

	override fun clearColor() {
		viewModelScope.launch {
			utilitySettings.saveInt("picker_color", Color.White.toArgb())
			utilitySettings.saveBoolean("picker_color_active", false)
		}
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
			override val onPickerClick = { toggleColorActive(!isColorActive.value) }
			override val pickerColor = if (isColorActive.value) colorSelected.value else Color.Transparent
			override val isSwatchActive = isColorActive.value
			override val swatchIcon: androidx.compose.ui.graphics.vector.ImageVector
				get() = Icons.Default.Clear
			override val text = _text.value
			override val onTextChange = { newText: String -> updateText(newText) }
			override val placeholderText = this@ViewModelPicker.placeholderText
			override val buttonText = this@ViewModelPicker.buttonText
			override val isButtonActive = !_isLoading.value
			override val onButtonClick = { this@ViewModelPicker.onButtonClick() }
		}
}
