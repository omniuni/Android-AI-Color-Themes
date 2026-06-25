package com.omniimpact.aicolorthemes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.hilt.navigation.compose.hiltViewModel
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
			AIColorThemesTheme(darkTheme = viewModelSettings.isDarkTheme) {
				Surface {
					ComposableAppNavHost()
				}
			}
		}
	}
}
