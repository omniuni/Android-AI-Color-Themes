package com.omniimpact.aicolorthemes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import com.omniimpact.aicolorthemes.ui.composable.app.ComposableAppNavHost
import com.omniimpact.aicolorthemes.ui.theme.AIColorThemesTheme
import com.omniimpact.aicolorthemes.viewmodel.picker.ViewModelPicker
import com.omniimpact.aicolorthemes.viewmodel.settings.ViewModelSettings

class MainActivity : ComponentActivity() {
	private val viewModelSettings: ViewModelSettings by viewModels()
	private val viewModelPicker: ViewModelPicker by viewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			AIColorThemesTheme(darkTheme = viewModelSettings.isDarkTheme) {
				Surface {
					ComposableAppNavHost(
						viewModelSettings = viewModelSettings,
						viewModelPicker = viewModelPicker
					)
				}
			}
		}
	}
}
