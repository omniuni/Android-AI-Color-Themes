package com.omniimpact.aicolorthemes

import androidx.compose.ui.graphics.Color
import com.omniimpact.aicolorthemes.database.entity.ThemeColorEntity
import com.omniimpact.aicolorthemes.database.entity.ThemeEntity
import com.omniimpact.aicolorthemes.database.dao.ThemeDao
import com.omniimpact.aicolorthemes.model.IDeepSeekQuery
import com.omniimpact.aicolorthemes.model.ModelColorTheme
import com.omniimpact.aicolorthemes.model.ModelSingleColor
import com.omniimpact.aicolorthemes.model.ThemeModel
import com.omniimpact.aicolorthemes.repository.ThemeRepository
import com.omniimpact.aicolorthemes.utility.IDeepSeekResult
import com.omniimpact.aicolorthemes.utility.IUtilityDeepSeekQuery
import com.omniimpact.aicolorthemes.utility.IUtilitySettings
import com.omniimpact.aicolorthemes.viewmodel.home.ViewModelHome
import com.omniimpact.aicolorthemes.viewmodel.picker.ViewModelPicker
import com.omniimpact.aicolorthemes.viewmodel.settings.ViewModelSettings
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.MockedStatic
import org.mockito.Mockito

@OptIn(ExperimentalCoroutinesApi::class)
class ThemeRepositoryTest {

	private val moshi = Moshi.Builder()
		.addLast(KotlinJsonAdapterFactory())
		.build()

	private lateinit var fakeThemeDao: FakeThemeDao
	private lateinit var fakeDeepSeekQuery: FakeUtilityDeepSeekQuery
	private lateinit var fakeUtilitySettings: FakeUtilitySettings
	private lateinit var themeRepository: ThemeRepository
	private lateinit var mockedColor: MockedStatic<android.graphics.Color>

	@Before
	fun setUp() {
		Dispatchers.setMain(UnconfinedTestDispatcher())
		fakeThemeDao = FakeThemeDao()
		fakeDeepSeekQuery = FakeUtilityDeepSeekQuery()
		fakeUtilitySettings = FakeUtilitySettings()
		themeRepository = ThemeRepository(
			utilityDeepSeekQuery = fakeDeepSeekQuery,
			themeDao = fakeThemeDao,
			ioDispatcher = Dispatchers.Unconfined
		)

		mockedColor = Mockito.mockStatic(android.graphics.Color::class.java)
		mockedColor.`when`<Int> { android.graphics.Color.parseColor(Mockito.anyString()) }.thenAnswer { invocation ->
			val arg = invocation.getArgument<String>(0)
			println("Color.parseColor called with: '$arg', length: ${arg.length}")
			if (arg.startsWith("#") && (arg.length == 7 || arg.length == 9)) {
                0xFFFFFF // Mocked parsed color int
			} else {
				throw IllegalArgumentException("Unknown color")
			}
		}
	}

	@After
	fun tearDown() {
		Dispatchers.resetMain()
		mockedColor.close()
	}

	// --- Moshi and Core Logic Tests ---

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

	// --- ThemeRepository Logic Tests ---

	@Test
	fun testThemesCombiningAndSortingLogic() = runBlocking {
		fakeThemeDao.insertTheme(ThemeEntity(id = 1, title = "Theme A", description = "Desc A", isFavorite = false, dateCreated = 1000))
		fakeThemeDao.insertThemeColors(listOf(
			ThemeColorEntity(keyThemeId = 1, colorIndex = 1, colorHex = "#FFFFFF"),
			ThemeColorEntity(keyThemeId = 1, colorIndex = 0, colorHex = "#000000")
		))

		val combinedThemes = themeRepository.themes.first()
		assertEquals(1, combinedThemes.size)
		assertEquals("Theme A", combinedThemes[0].themeName)
		assertEquals(2, combinedThemes[0].colorTheme.size)
		// Colors must be sorted by colorIndex (0 then 1)
		assertEquals("#000000", combinedThemes[0].colorTheme[0])
		assertEquals("#FFFFFF", combinedThemes[0].colorTheme[1])
	}

	@Test
	fun testCreateThemeFiltersInvalidHexColors() = runBlocking {
		val successQuery = ModelColorTheme(
			themeName = "Forest",
			themeDescription = "Green shades",
			colorTheme = listOf("#123456", "invalid_color_string", "#abcdef")
		)
		fakeDeepSeekQuery.responseFlow = flowOf(IDeepSeekResult.Loading, IDeepSeekResult.Success(successQuery))

		val results = themeRepository.createTheme(ModelColorTheme()).toList()

		assertEquals(2, results.size)
		assertTrue(results[0] is IDeepSeekResult.Loading)
		assertTrue(results[1] is IDeepSeekResult.Success)

		val successResult = (results[1] as IDeepSeekResult.Success).data
		// The invalid hex must be filtered out by toColorInt() validation check
		assertEquals(2, successResult.colorTheme.size)
		assertEquals("#123456", successResult.colorTheme[0])
		assertEquals("#abcdef", successResult.colorTheme[1])

		// DB should have been updated with only valid colors
		assertEquals(1, fakeThemeDao.themesList.size)
		assertEquals("Forest", fakeThemeDao.themesList[0].title)
		assertEquals(2, fakeThemeDao.colorsList.size)
		assertEquals("#123456", fakeThemeDao.colorsList[0].colorHex)
	}

	@Test
	fun testRemoveThemeDeletesFromDatabase() = runBlocking {
		fakeThemeDao.insertTheme(ThemeEntity(id = 15, title = "To Delete", description = "", isFavorite = false, dateCreated = 0))
		fakeThemeDao.insertThemeColors(listOf(ThemeColorEntity(keyThemeId = 15, colorIndex = 0, colorHex = "#123456")))

		val themeToRemove = ModelColorTheme(themeName = "To Delete", id = 15)
		themeRepository.removeTheme(themeToRemove)

		// Verification is run on unconfined dispatcher so it executes synchronously
		assertTrue(fakeThemeDao.themesList.isEmpty())
		assertTrue(fakeThemeDao.colorsList.isEmpty())
	}

	// --- ViewModels Logic Tests ---

	@Test
	fun testViewModelHomeColorSelectedMapping() = runBlocking {
		fakeUtilitySettings.saveInt("picker_color", 0xFFFF00FF.toInt())
		val viewModelHome = ViewModelHome(fakeUtilitySettings, themeRepository)

		val colorValue = viewModelHome.colorSelected.first()
		assertEquals(Color(0xFFFF00FF.toInt()), colorValue)
	}

	@Test
	fun testViewModelPickerUpdateColorAndText() = runBlocking {
		val viewModelPicker = ViewModelPicker(fakeUtilitySettings, themeRepository)

		viewModelPicker.updateColor(Color.Yellow, shouldActivate = true)
		val colorValue = viewModelPicker.colorSelected.first()
		assertEquals(Color.Yellow, colorValue)

		viewModelPicker.updateText("Retro Theme")
		assertEquals("Retro Theme", viewModelPicker.text.value)
	}

	@Test
	fun testViewModelSettingsSelectTheme() = runBlocking {
		val viewModelSettings = ViewModelSettings(fakeUtilitySettings)

		viewModelSettings.selectTheme(ThemeModel("Dark", "dark"))
		val selected = viewModelSettings.themeSelected.first()
		assertEquals("dark", selected.key)

		val savedKey = fakeUtilitySettings.getStringFlow("theme_key", "").first()
		assertEquals("dark", savedKey)
	}

	// --- Supporting Fakes ---

	class FakeThemeDao : ThemeDao() {
		val themesList = mutableListOf<ThemeEntity>()
		val colorsList = mutableListOf<ThemeColorEntity>()
		private var currentId = 1L

		private val themesFlow = MutableStateFlow<List<ThemeEntity>>(emptyList())
		private val colorsFlow = MutableStateFlow<List<ThemeColorEntity>>(emptyList())

		private fun updateFlows() {
			themesFlow.value = ArrayList(themesList)
			colorsFlow.value = ArrayList(colorsList)
		}

		override suspend fun insertTheme(theme: ThemeEntity): Long {
			val withId = if (theme.id == 0L) theme.copy(id = currentId++) else theme
			themesList.add(withId)
			updateFlows()
			return withId.id
		}

		override suspend fun insertThemeColors(colors: List<ThemeColorEntity>) {
			colorsList.addAll(colors)
			updateFlows()
		}

		override fun getThemesFlow(): Flow<List<ThemeEntity>> = themesFlow

		override suspend fun getColorsForTheme(themeId: Long): List<ThemeColorEntity> {
			return colorsList.filter { it.keyThemeId == themeId }
		}

		override fun getAllColorsFlow(): Flow<List<ThemeColorEntity>> = colorsFlow

		override suspend fun deleteTheme(theme: ThemeEntity) {
			themesList.removeIf { it.id == theme.id }
			updateFlows()
		}

		override suspend fun deleteThemeById(id: Long) {
			themesList.removeIf { it.id == id }
			updateFlows()
		}

		override suspend fun deleteColorsByThemeId(themeId: Long) {
			colorsList.removeIf { it.keyThemeId == themeId }
			updateFlows()
		}

		override suspend fun clearAllThemes() {
			themesList.clear()
			colorsList.clear()
			updateFlows()
		}

		override suspend fun updateFavoriteStatus(themeId: Long, isFavorite: Boolean) {
			val index = themesList.indexOfFirst { it.id == themeId }
			if (index != -1) {
				themesList[index] = themesList[index].copy(isFavorite = isFavorite)
			}
			updateFlows()
		}
	}

	class FakeUtilityDeepSeekQuery : IUtilityDeepSeekQuery {
		var responseFlow: Flow<IDeepSeekResult<IDeepSeekQuery>> = flowOf(IDeepSeekResult.Failure("No custom handler set"))

		@Suppress("UNCHECKED_CAST")
		override fun <T : IDeepSeekQuery> send(query: T): Flow<IDeepSeekResult<T>> {
			return responseFlow as Flow<IDeepSeekResult<T>>
		}
	}

	class FakeUtilitySettings : IUtilitySettings {
		val data = mutableMapOf<String, Any>()

		override fun getStringFlow(key: String, defaultValue: String): Flow<String> = flow {
			emit(data[key] as? String ?: defaultValue)
		}

		override suspend fun saveString(key: String, value: String) {
			data[key] = value
		}

		override fun getBooleanFlow(key: String, defaultValue: Boolean): Flow<Boolean> = flow {
			emit(data[key] as? Boolean ?: defaultValue)
		}

		override suspend fun saveBoolean(key: String, value: Boolean) {
			data[key] = value
		}

		override fun getIntFlow(key: String, defaultValue: Int): Flow<Int> = flow {
			emit(data[key] as? Int ?: defaultValue)
		}

		override suspend fun saveInt(key: String, value: Int) {
			data[key] = value
		}
	}
}
