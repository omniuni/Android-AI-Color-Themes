package com.omniimpact.aicolorthemes.ui.composable

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.omniimpact.aicolorthemes.model.ModelColorTheme
import com.omniimpact.aicolorthemes.ui.composable.home.ComposableRefineBottomSheet
import com.omniimpact.aicolorthemes.ui.composable.home.ComposableThemeItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ComposableThemeItemAndBottomSheetTest {

	@get:Rule
	val composeTestRule = createComposeRule()

	private val testTheme = ModelColorTheme(
		promptQuery = "",
		themeName = "Warm Sunset",
		themeDescription = "Beautiful warm gradient.",
		colorTheme = listOf("#FF5733", "#C70039", "#900C3F")
	)

	@Test
	fun themeItem_rendersCorrectly_whenButtonsVisible() {
		var refineClicked = false
		var removeClicked = false

		composeTestRule.setContent {
			ComposableThemeItem(
				theme = testTheme,
				showRefineButton = true,
				showRemoveButton = true,
				onRefine = { refineClicked = true },
				onRemove = { removeClicked = true }
			)
		}

		composeTestRule.onNodeWithText("Warm Sunset").assertExists()
		composeTestRule.onNodeWithText("Beautiful warm gradient.").assertExists()
		composeTestRule.onNodeWithText("#FF5733").assertExists()

		// Verify buttons are visible
		composeTestRule.onNodeWithText("Refine").assertExists()
		composeTestRule.onNodeWithContentDescription("Remove").assertExists()

		// Perform clicks
		composeTestRule.onNodeWithText("Refine").performClick()
		composeTestRule.onNodeWithContentDescription("Remove").performClick()

		assertTrue(refineClicked)
		assertTrue(removeClicked)
	}

	@Test
	fun themeItem_hidesButtons_whenInstructed() {
		composeTestRule.setContent {
			ComposableThemeItem(
				theme = testTheme,
				showRefineButton = false,
				showRemoveButton = false
			)
		}

		// Verify buttons are not visible
		composeTestRule.onNodeWithText("Refine").assertDoesNotExist()
		composeTestRule.onNodeWithContentDescription("Remove").assertDoesNotExist()
	}

	@Test
	fun refineBottomSheet_submitsSuccessfully_whenTextEntered() {
		var submitted = false
		var submittedTheme: ModelColorTheme? = null
		var submittedText = ""
		var dismissed = false

		composeTestRule.setContent {
			ComposableRefineBottomSheet(
				theme = testTheme,
				onDismiss = { dismissed = true },
				onSubmit = { theme, text ->
					submitted = true
					submittedTheme = theme
					submittedText = text
				}
			)
		}

		// Verify title and item
		composeTestRule.onNodeWithText("Refine Theme").assertExists()
		composeTestRule.onNodeWithText("Warm Sunset").assertExists()
		
		// The bottom sheet theme item has buttons hidden by default
		composeTestRule.onNodeWithText("Refine").assertDoesNotExist()
		composeTestRule.onNodeWithContentDescription("Remove").assertDoesNotExist()

		// The Create button should be disabled initially (empty text)
		composeTestRule.onNodeWithText("Create").assertIsNotEnabled()

		// Enter input
		composeTestRule.onNodeWithText("Describe desired changes...").performTextInput("Make it slightly cooler")

		// Create button should now be enabled
		composeTestRule.onNodeWithText("Create").assertIsEnabled()

		// Submit
		composeTestRule.onNodeWithText("Create").performClick()

		assertTrue(submitted)
		assertEquals(testTheme, submittedTheme)
		assertEquals("Make it slightly cooler", submittedText)
		assertTrue(dismissed)
	}

	@Test
	fun refineBottomSheet_dismisses_onCloseClick() {
		var dismissed = false

		composeTestRule.setContent {
			ComposableRefineBottomSheet(
				theme = testTheme,
				onDismiss = { dismissed = true },
				onSubmit = { _: ModelColorTheme, _: String -> }
			)
		}

		composeTestRule.onNodeWithContentDescription("Close").performClick()
		assertTrue(dismissed)
	}
}