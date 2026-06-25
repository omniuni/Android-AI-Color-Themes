package com.omniimpact.aicolorthemes

import com.omniimpact.aicolorthemes.model.IDeepSeekQuery
import com.omniimpact.aicolorthemes.model.ModelColorTheme
import com.omniimpact.aicolorthemes.model.ModelSingleColor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ThemeRepositoryTest {

	private val moshi = Moshi.Builder()
		.addLast(KotlinJsonAdapterFactory())
		.build()

	@Test
	fun testMoshiDeserializesModelColorTheme() {
		val json = """
			{
				"themeName": "Sunset Glow",
				"themeDescription": "A warm color theme inspired by beautiful sunsets.",
				"colorTheme": ["#FF5733", "#FFC300", "#C70039"]
			}
		""".trimIndent()

		val adapter = moshi.adapter(ModelColorTheme::class.java)
		val deserialized = adapter.fromJson(json)

		assertNotNull(deserialized)
		assertEquals("Sunset Glow", deserialized?.themeName)
		assertEquals("A warm color theme inspired by beautiful sunsets.", deserialized?.themeDescription)
		assertEquals(3, deserialized?.colorTheme?.size)
		assertEquals("#FF5733", deserialized?.colorTheme?.get(0))
		assertEquals("#FFC300", deserialized?.colorTheme?.get(1))
		assertEquals("#C70039", deserialized?.colorTheme?.get(2))
	}

	@Test
	fun testMoshiDeserializesModelSingleColor() {
		val json = """
			{
				"colorHex": "#E0F7FA"
			}
		""".trimIndent()

		val adapter = moshi.adapter(ModelSingleColor::class.java)
		val deserialized = adapter.fromJson(json)

		assertNotNull(deserialized)
		assertEquals("#E0F7FA", deserialized?.colorHex)
	}

	@Test
	fun testPromptPreservationLogic() {
		val originalQuery: IDeepSeekQuery = ModelColorTheme(
			promptSystem = "System Instructions",
			promptQuery = "User Instructions",
			themeName = "",
			themeDescription = "",
			colorTheme = emptyList()
		)

		val json = """
			{
				"themeName": "Ocean Breeze",
				"themeDescription": "Cool shades of blue and teal.",
				"colorTheme": ["#006064", "#00838F", "#0097A7"]
			}
		""".trimIndent()

		val adapter = moshi.adapter(originalQuery::class.java)
		val deserialized = adapter.fromJson(json)
		assertNotNull(deserialized)

		val result = (deserialized as IDeepSeekQuery).copyWithPrompts(
			originalQuery.promptSystem,
			originalQuery.promptQuery
		)

		assertEquals("System Instructions", result.promptSystem)
		assertEquals("User Instructions", result.promptQuery)
		assertEquals("Ocean Breeze", (result as ModelColorTheme).themeName)
	}
}
