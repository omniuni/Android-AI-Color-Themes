package com.omniimpact.aicolorthemes.network

import com.squareup.moshi.Json
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class ChatMessage(
	val role: String,
	val content: String
)

data class ResponseFormat(
	val type: String = "json_object"
)

data class ChatRequest(
	val model: String = "deepseek-v4-flash",
	val messages: List<ChatMessage>,
	@field:Json(name = "response_format") val responseFormat: ResponseFormat = ResponseFormat()
)

data class ChoiceMessage(
	val content: String
)

data class ChatChoice(
	val message: ChoiceMessage
)

data class ChatResponse(
	val choices: List<ChatChoice>
)

interface DeepSeekApiService {
	@POST("chat/completions")
	suspend fun chatCompletions(
		@Header("Authorization") authorization: String,
		@Body request: ChatRequest
	): ChatResponse
}
