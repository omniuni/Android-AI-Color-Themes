package com.omniimpact.aicolorthemes.ui.composable

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.omniimpact.aicolorthemes.ui.composable.home.ComposableThemeCreationRow
import com.omniimpact.aicolorthemes.ui.composable.home.IComposableThemeCreationRow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ComposableThemeCreationRowTest {

	@get:Rule
	val composeTestRule = createComposeRule()

	class TestThemeCreationRowState(
		override val onPickerClick: () -> Unit = {},
		override val pickerColor: Color = Color.Red,
		override val isSwatchActive: Boolean = true,
		override val text: String = "Test",
		override val onTextChange: (String) -> Unit = {},
		override val placeholderText: String = "Placeholder",
		override val buttonText: String = "Create",
		override val isButtonActive: Boolean = true,
		override val onButtonClick: () -> Unit = {}
	) : IComposableThemeCreationRow

	@Test
	fun whenSwatchIsInactive_showsCloseIcon() {
		val state = TestThemeCreationRowState(isSwatchActive = false)
		composeTestRule.setContent {
			ComposableThemeCreationRow(state = state)
		}

		composeTestRule.onNodeWithContentDescription("Inactive").assertExists()
	}

	@Test
	fun whenSwatchIsActive_doesNotShowCloseIcon() {
		val state = TestThemeCreationRowState(isSwatchActive = true)
		composeTestRule.setContent {
			ComposableThemeCreationRow(state = state)
		}

		composeTestRule.onNodeWithContentDescription("Inactive").assertDoesNotExist()
	}

	@Test
	fun whenButtonIsActive_buttonIsEnabled() {
		val state = TestThemeCreationRowState(isButtonActive = true, buttonText = "Create")
		composeTestRule.setContent {
			ComposableThemeCreationRow(state = state)
		}

		composeTestRule.onNodeWithText("Create").assertIsEnabled()
	}

	@Test
	fun whenButtonIsInactive_buttonIsDisabled() {
		val state = TestThemeCreationRowState(isButtonActive = false, buttonText = "Create")
		composeTestRule.setContent {
			ComposableThemeCreationRow(state = state)
		}

		composeTestRule.onNodeWithText("Create").assertIsNotEnabled()
	}
}
