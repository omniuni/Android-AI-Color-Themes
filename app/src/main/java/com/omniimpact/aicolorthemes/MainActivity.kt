package com.omniimpact.aicolorthemes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.omniimpact.aicolorthemes.ui.composable.app.ComposableAppNavHost
import com.omniimpact.aicolorthemes.ui.theme.AIColorThemesTheme
import com.omniimpact.aicolorthemes.viewmodel.settings.ViewModelSettings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			val viewModelSettings: ViewModelSettings = hiltViewModel()
			val themeSelected by viewModelSettings.themeSelected.collectAsStateWithLifecycle()
			val isDarkTheme = themeSelected.key == "dark" || themeSelected.key == "m3_dark" || themeSelected.key == "dynamic_dark"
			AIColorThemesTheme(themeKey = themeSelected.key, darkTheme = isDarkTheme) {
				Surface {
					ComposableAppNavHost()
				}
			}
		}
	}
}
