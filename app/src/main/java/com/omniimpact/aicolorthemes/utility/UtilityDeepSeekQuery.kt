package com.omniimpact.aicolorthemes.utility

import android.content.Context
import com.omniimpact.aicolorthemes.model.IDeepSeekQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object UtilityDeepSeekQuery {

    fun <T : IDeepSeekQuery> send(context: Context, query: T, callback: IDeepSeekResult<T>) {
        val apiKey = UtilitySettings(context).getString("api_key", "")
        if (apiKey.isEmpty()) {
            ClassLog.e(UtilityDeepSeekQuery::class, "No API key saved")
            callback.onFailure("No API key saved")
            return
        }

        val exampleJson = JSONObject()
        val fields = query::class.java.declaredFields
        for (field in fields) {
            if (field.name != "INSTANCE" && field.name != "Companion" && field.name != "serialVersionUID" && 
                field.name != "promptSystem" && field.name != "promptQuery" && field.name != "\$stable") {
                field.isAccessible = true
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
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("https://api.deepseek.com/chat/completions")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Authorization", "Bearer $apiKey")
                connection.doOutput = true

                val jsonPayload = JSONObject()
                jsonPayload.put("model", "deepseek-v4-flash")
                val messages = org.json.JSONArray()
                val systemMessage = JSONObject().apply {
                    put("role", "system")
                    put("content", "${query.promptSystem} Respond in JSON format matching this structure: $exampleJson")
                }
                val userMessage = JSONObject().apply {
                    put("role", "user")
                    put("content", query.promptQuery)
                }
                messages.put(systemMessage)
                messages.put(userMessage)
                jsonPayload.put("messages", messages)
                jsonPayload.put("response_format", JSONObject().put("type", "json_object"))

                ClassLog.d(UtilityDeepSeekQuery::class, "Payload: $jsonPayload")

                val writer = OutputStreamWriter(connection.outputStream)
                writer.write(jsonPayload.toString())
                writer.flush()
                writer.close()

                ClassLog.d(UtilityDeepSeekQuery::class, "Response code: ${connection.responseCode}")
                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    ClassLog.d(UtilityDeepSeekQuery::class, "Response: $response")
                    val jsonResponse = JSONObject(response)
                    val content = jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content")
                    ClassLog.d(UtilityDeepSeekQuery::class, "Content: $content")
                    
                    val resultJson = JSONObject(content)
                    val clazz = query::class.java
                    val constructor = clazz.constructors[0]

                    val args = constructor.parameterTypes.map { type ->
                        when {
                            type == String::class.java -> ""
                            type == Int::class.java || type == Int::class.javaPrimitiveType || type == Int::class.javaObjectType -> 0
                            type == Boolean::class.java || type == Boolean::class.javaPrimitiveType || type == Boolean::class.javaObjectType -> false
                            type == Double::class.java || type == Double::class.javaPrimitiveType || type == Double::class.javaObjectType -> 0.0
                            type == Float::class.java || type == Float::class.javaPrimitiveType || type == Float::class.javaObjectType -> 0.0f
                            type == Long::class.java || type == Long::class.javaPrimitiveType || type == Long::class.javaObjectType -> 0L
                            type == List::class.java -> emptyList<Any>()
                            else -> null
                        }
                    }.toTypedArray<Any?>()

                    @Suppress("UNCHECKED_CAST")
                    val result = constructor.newInstance(*args) as T

                    clazz.declaredFields.forEach { field ->
                        if (field.name == "INSTANCE" || field.name == "Companion" || field.name == "serialVersionUID") return@forEach
                        field.isAccessible = true
                        try {
                            if (resultJson.has(field.name) && !resultJson.isNull(field.name)) {
                                val value = resultJson.get(field.name)
                                if (field.type == String::class.java) {
                                    field.set(result, value.toString())
                                } else if (field.type == Int::class.java || field.type == Int::class.javaObjectType) {
                                    field.set(result, (value as Number).toInt())
                                } else if (field.type == Double::class.java || field.type == Double::class.javaObjectType) {
                                    field.set(result, (value as Number).toDouble())
                                } else if (field.type == Float::class.java || field.type == Float::class.javaObjectType) {
                                    field.set(result, (value as Number).toFloat())
                                } else if (field.type == List::class.java) {
                                    val list = mutableListOf<Any>()
                                    if (value is org.json.JSONArray) {
                                        for (i in 0 until value.length()) {
                                            list.add(value.get(i))
                                        }
                                    }
                                    field.set(result, list)
                                } else {
                                    field.set(result, value)
                                }
                            } else if (field.name == "promptSystem") {
                                field.set(result, query.promptSystem)
                            } else if (field.name == "promptQuery") {
                                field.set(result, query.promptQuery)
                            }
                        } catch (e: Exception) {
                            ClassLog.e(UtilityDeepSeekQuery::class, "Error setting field ${field.name}: ${e.message}")
                        }
                    }
                    callback.onSuccess(result)

                } else {
                    val errorStream = connection.errorStream?.bufferedReader()?.use { it.readText() }
                    ClassLog.e(UtilityDeepSeekQuery::class, "Error: ${connection.responseCode}, $errorStream")
                    callback.onFailure("Error: ${connection.responseCode}")
                }
            } catch (e: Exception) {
                ClassLog.e(UtilityDeepSeekQuery::class, "Exception: ${e.message}", e)
                callback.onFailure(e.message ?: "Unknown error")
            }
        }
    }
}