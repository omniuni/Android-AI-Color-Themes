package com.omniimpact.aicolorthemes.utility

import com.omniimpact.aicolorthemes.model.IDeepSeekQuery
import com.omniimpact.aicolorthemes.network.ChatChoice
import com.omniimpact.aicolorthemes.network.ChatRequest
import com.omniimpact.aicolorthemes.network.ChatResponse
import com.omniimpact.aicolorthemes.network.ChoiceMessage
import com.omniimpact.aicolorthemes.network.DeepSeekApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class UtilityDeepSeekQueryTest {

	private lateinit var fakeUtilitySettings: FakeUtilitySettings
	private lateinit var fakeDeepSeekApiService: FakeDeepSeekApiService
	private lateinit var moshi: Moshi
	private lateinit var utilityDeepSeekQuery: UtilityDeepSeekQuery

	@Before
	fun setUp() {
		fakeUtilitySettings = FakeUtilitySettings()
		fakeDeepSeekApiService = FakeDeepSeekApiService()
		moshi = Moshi.Builder()
			.addLast(KotlinJsonAdapterFactory())
			.build()
		// Use Dispatchers.Unconfined for synchronous, predictable coroutines execution in tests
		utilityDeepSeekQuery = UtilityDeepSeekQuery(
			utilitySettings = fakeUtilitySettings,
			moshi = moshi,
			deepSeekApiService = fakeDeepSeekApiService,
			ioDispatcher = Dispatchers.Unconfined
		)
	}

	// --- Supporting File (FakeUtilitySettings) Tests ---

	@Test
	fun testFakeUtilitySettingsSaveAndReadString() = runBlocking {
		fakeUtilitySettings.saveString("api_key", "test_key")
		val apiKey = fakeUtilitySettings.getStringFlow("api_key", "").first()
		assertEquals("test_key", apiKey)
	}

	@Test
	fun testFakeUtilitySettingsSaveAndReadBoolean() = runBlocking {
		fakeUtilitySettings.saveBoolean("some_bool", true)
		val value = fakeUtilitySettings.getBooleanFlow("some_bool", false).first()
		assertTrue(value)
	}

	@Test
	fun testFakeUtilitySettingsSaveAndReadInt() = runBlocking {
		fakeUtilitySettings.saveInt("some_int", 42)
		val value = fakeUtilitySettings.getIntFlow("some_int", 0).first()
		assertEquals(42, value)
	}

	// --- UtilityDeepSeekQuery Tests ---

	@Test
	fun testSend_Negative_NoApiKey() = runBlocking {
		val query = TestQuery(
			promptSystem = "System prompt",
			promptQuery = "User prompt",
			themeName = "Not Set",
			themeDescription = "Not Set",
			colorTheme = emptyList()
		)

		val results = utilityDeepSeekQuery.send(query).toList()

		assertEquals(2, results.size)
		assertTrue(results[0] is IDeepSeekResult.Loading)
		assertTrue(results[1] is IDeepSeekResult.Failure)
		assertEquals("No API key saved", (results[1] as IDeepSeekResult.Failure).message)
	}

	@Test
	fun testSend_Positive_SuccessfulResponse() = runBlocking {
		fakeUtilitySettings.saveString("api_key", "valid_api_key")

		val query = TestQuery(
			promptSystem = "System prompt",
			promptQuery = "User prompt",
			themeName = "",
			themeDescription = "",
			colorTheme = emptyList()
		)

		fakeDeepSeekApiService.chatResponseHandler = { authHeader, request ->
			assertEquals("Bearer valid_api_key", authHeader)
			val systemMessage = request.messages[0].content
			assertTrue(systemMessage.contains("System prompt Respond in JSON format matching this structure:"))
			assertTrue(systemMessage.contains("\"themeDescription\":\"string\""))
			assertTrue(systemMessage.contains("\"themeName\":\"string\""))
			assertTrue(systemMessage.contains("\"colorTheme\":[\"string\"]"))
			assertEquals("User prompt", request.messages[1].content)

			ChatResponse(
				choices = listOf(
					ChatChoice(
						message = ChoiceMessage(
							content = """
								{
									"themeName": "Forest Walk",
									"themeDescription": "Shades of green and brown",
									"colorTheme": ["#2E7D32", "#388E3C", "#8D6E63"]
								}
							""".trimIndent()
						)
					)
				)
			)
		}

		val results = utilityDeepSeekQuery.send(query).toList()

		assertEquals(2, results.size)
		assertTrue(results[0] is IDeepSeekResult.Loading)
		assertTrue(results[1] is IDeepSeekResult.Success)

		val successResult = (results[1] as IDeepSeekResult.Success).data
		assertEquals("Forest Walk", successResult.themeName)
		assertEquals("Shades of green and brown", successResult.themeDescription)
		assertEquals(3, successResult.colorTheme.size)
		assertEquals("#2E7D32", successResult.colorTheme[0])
		assertEquals("System prompt", successResult.promptSystem)
		assertEquals("User prompt", successResult.promptQuery)
	}

	@Test
	fun testSend_Negative_ApiServiceThrowsException() = runBlocking {
		fakeUtilitySettings.saveString("api_key", "valid_api_key")

		val query = TestQuery(
			promptSystem = "System prompt",
			promptQuery = "User prompt",
			themeName = "",
			themeDescription = "",
			colorTheme = emptyList()
		)

		fakeDeepSeekApiService.shouldThrowException = IOException("Network connection lost")

		val results = utilityDeepSeekQuery.send(query).toList()

		assertEquals(2, results.size)
		assertTrue(results[0] is IDeepSeekResult.Loading)
		assertTrue(results[1] is IDeepSeekResult.Failure)
		assertEquals("Network connection lost", (results[1] as IDeepSeekResult.Failure).message)
	}

	@Test
	fun testSend_Negative_EmptyResponseContent() = runBlocking {
		fakeUtilitySettings.saveString("api_key", "valid_api_key")

		val query = TestQuery(
			promptSystem = "System prompt",
			promptQuery = "User prompt",
			themeName = "",
			themeDescription = "",
			colorTheme = emptyList()
		)

		// API Service returns empty choices list
		fakeDeepSeekApiService.chatResponseHandler = { _, _ ->
			ChatResponse(choices = emptyList())
		}

		val results = utilityDeepSeekQuery.send(query).toList()

		assertEquals(2, results.size)
		assertTrue(results[0] is IDeepSeekResult.Loading)
		assertTrue(results[1] is IDeepSeekResult.Failure)
		assertEquals("Empty response content from AI", (results[1] as IDeepSeekResult.Failure).message)
	}

	@Test
	fun testSend_Negative_MoshiDeserializationFailure() = runBlocking {
		fakeUtilitySettings.saveString("api_key", "valid_api_key")

		val query = TestQuery(
			promptSystem = "System prompt",
			promptQuery = "User prompt",
			themeName = "",
			themeDescription = "",
			colorTheme = emptyList()
		)

		// API returns completely invalid JSON content
		fakeDeepSeekApiService.chatResponseHandler = { _, _ ->
			ChatResponse(
				choices = listOf(
					ChatChoice(
						message = ChoiceMessage(
							content = "Invalid Non-JSON Content"
						)
					)
				)
			)
		}

		val results = utilityDeepSeekQuery.send(query).toList()

		assertEquals(2, results.size)
		assertTrue(results[0] is IDeepSeekResult.Loading)
		assertTrue(results[1] is IDeepSeekResult.Failure)
		// Moshi should throw parsing exception which gets caught in the catch block and returned as Failure(message)
		val failureMsg = (results[1] as IDeepSeekResult.Failure).message
		assertTrue(failureMsg.contains("Required value") || failureMsg.contains("Expected") || failureMsg.contains("Use JsonReader.setLenient"))
	}

	// --- Fakes and Test Models ---

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

	class FakeDeepSeekApiService : DeepSeekApiService {
		var chatResponseHandler: ((String, ChatRequest) -> ChatResponse)? = null
		var shouldThrowException: Exception? = null

		override suspend fun chatCompletions(authorization: String, request: ChatRequest): ChatResponse {
			shouldThrowException?.let { throw it }
			return chatResponseHandler?.invoke(authorization, request)
				?: ChatResponse(choices = emptyList())
		}
	}

	data class TestQuery(
		override val promptSystem: String = "",
		override val promptQuery: String = "",
		val themeName: String = "",
		val themeDescription: String = "",
		val colorTheme: List<String> = emptyList()
	) : IDeepSeekQuery {
		override fun copyWithPrompts(promptSystem: String, promptQuery: String): IDeepSeekQuery {
			return copy(promptSystem = promptSystem, promptQuery = promptQuery)
		}
	}
}
