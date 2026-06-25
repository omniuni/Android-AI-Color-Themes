package com.omniimpact.aicolorthemes.utility

import com.omniimpact.aicolorthemes.di.IoDispatcher
import com.omniimpact.aicolorthemes.model.IDeepSeekQuery
import com.omniimpact.aicolorthemes.network.ChatMessage
import com.omniimpact.aicolorthemes.network.ChatRequest
import com.omniimpact.aicolorthemes.network.DeepSeekApiService
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility for performing AI queries using the DeepSeek API.
 */
@Singleton
class UtilityDeepSeekQuery @Inject constructor(
	private val utilitySettings: UtilitySettings,
	private val moshi: Moshi,
	private val deepSeekApiService: DeepSeekApiService,
	@param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

	/**
	 * Sends an AI query to the DeepSeek API and streams the result states via a Flow.
	 *
	 * @param query The query object containing the system and user prompts.
	 * @param T The type of the query and result model.
	 * @return A Flow of IDeepSeekResult representing Loading, Success, or Failure.
	 */
	fun <T : IDeepSeekQuery> send(query: T): Flow<IDeepSeekResult<T>> = flow {
		emit(IDeepSeekResult.Loading)

		val apiKey = utilitySettings.getStringFlow("api_key", "").first()
		if (apiKey.isEmpty()) {
			ClassLog.e(UtilityDeepSeekQuery::class, "No API key saved")
			emit(IDeepSeekResult.Failure("No API key saved"))
			return@flow
		}

		// Use reflection to inspect the query object and generate an example JSON schema 
		// to guide the AI on the expected response format.
		val exampleJson = JSONObject()
		val fields = query::class.java.declaredFields
		for (field in fields) {
			// Skip metadata and helper fields during reflection
			if (field.name != "INSTANCE" && field.name != "Companion" && field.name != "serialVersionUID" && 
				field.name != "promptSystem" && field.name != "promptQuery" && field.name != "\$stable") {
				field.isAccessible = true // Ensure private fields can be accessed
				val type = field.type
				exampleJson.put(field.name, when {
					List::class.java.isAssignableFrom(type) -> org.json.JSONArray().put("string")
					type == Int::class.java || type == Int::class.javaObjectType -> 0
					type == Double::class.java || type == Double::class.javaObjectType || type == Float::class.java || type == Float::class.javaObjectType -> 0.0
					type == String::class.java -> "string"
					else -> "[match type]"
				})
			}
		}

		ClassLog.d(UtilityDeepSeekQuery::class, "Starting network request")
		try {
			val request = ChatRequest(
				messages = listOf(
					ChatMessage(
						role = "system",
						content = "${query.promptSystem} Respond in JSON format matching this structure: $exampleJson"
					),
					ChatMessage(
						role = "user",
						content = query.promptQuery
					)
				)
			)

			ClassLog.d(UtilityDeepSeekQuery::class, "Request payload: $request")
			val response = deepSeekApiService.chatCompletions("Bearer $apiKey", request)
			val content = response.choices.firstOrNull()?.message?.content
			ClassLog.d(UtilityDeepSeekQuery::class, "Content response: $content")

			if (content != null) {
				val adapter = moshi.adapter(query::class.java)
				val deserialized = adapter.fromJson(content)
				if (deserialized != null) {
					val result = copyWithOriginalPrompts(deserialized, query)
					emit(IDeepSeekResult.Success(result))
				} else {
					emit(IDeepSeekResult.Failure("Moshi deserialization returned null"))
				}
			} else {
				emit(IDeepSeekResult.Failure("Empty response content from AI"))
			}
		} catch (e: Exception) {
			ClassLog.e(UtilityDeepSeekQuery::class, "Exception: ${e.message}", e)
			emit(IDeepSeekResult.Failure(e.message ?: "Unknown error"))
		}
	}.flowOn(ioDispatcher)

	/**
	 * Copies the deserialized result with the original query's prompts.
	 */
	@Suppress("UNCHECKED_CAST")
	private fun <T : IDeepSeekQuery> copyWithOriginalPrompts(deserialized: T, query: T): T {
		return deserialized.copyWithPrompts(query.promptSystem, query.promptQuery) as T
	}
}
