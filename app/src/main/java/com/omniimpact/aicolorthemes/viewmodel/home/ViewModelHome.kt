package com.omniimpact.aicolorthemes.viewmodel.home

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omniimpact.aicolorthemes.model.ModelColorTheme
import com.omniimpact.aicolorthemes.utility.IDeepSeekResult
import com.omniimpact.aicolorthemes.repository.ThemeRepository
import com.omniimpact.aicolorthemes.ui.composable.home.IComposableThemeCreationRow
import com.omniimpact.aicolorthemes.utility.IUtilitySettings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Colorize
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

interface IViewModelHome {
	val colorSelected: StateFlow<Color>
	val isColorActive: StateFlow<Boolean>
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
	private val utilitySettings: IUtilitySettings,
	private val themeRepository: ThemeRepository
) : ViewModel(), IViewModelHome {

	override val colorSelected: StateFlow<Color> = utilitySettings.getIntFlow("picker_color", Color.Transparent.toArgb())
		.map { Color(it) }
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = Color.Transparent
		)

	override val isColorActive: StateFlow<Boolean> = utilitySettings.getBooleanFlow("picker_color_active", false)
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = false
		)

	private val _text = MutableStateFlow("")
	override val text: StateFlow<String> = _text.asStateFlow()
	
	override val themes: StateFlow<List<ModelColorTheme>> = themeRepository.themes

	private val _isLoading = MutableStateFlow(false)
	override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

	override fun updateText(newValue: String) {
		_text.value = newValue
	}

	override fun removeTheme(theme: ModelColorTheme) {
		themeRepository.removeTheme(theme)
	}

	override fun clearThemes() {
		themeRepository.clearThemes()
	}
	
	private fun createTheme() {
		val anchorColor = if (isColorActive.value) {
			"Anchor Color: #${Integer.toHexString(colorSelected.value.toArgb()).substring(2)} \n\n"
		} else ""
		
		val query = ModelColorTheme(
			promptQuery = anchorColor + _text.value,
			themeName = "",
			themeDescription = "",
			colorTheme = emptyList()
		)
		
		viewModelScope.launch {
			themeRepository.createTheme(query).collect { result ->
				when (result) {
					is IDeepSeekResult.Loading -> {
						_isLoading.value = true
					}
					is IDeepSeekResult.Success -> {
						_isLoading.value = false
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
			override val onPickerClick = {} 
			override val pickerColor: Color
				get() = if (isColorActive.value) colorSelected.value else Color.Transparent
			override val isSwatchActive: Boolean
				get() = isColorActive.value
			override val swatchIcon: androidx.compose.ui.graphics.vector.ImageVector
				get() = Icons.Default.Colorize
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
