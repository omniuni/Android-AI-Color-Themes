package com.omniimpact.aicolorthemes.utility

sealed interface IDeepSeekResult<out T> {
	object Loading : IDeepSeekResult<Nothing>
	data class Success<out T>(val data: T) : IDeepSeekResult<T>
	data class Failure(val message: String) : IDeepSeekResult<Nothing>
}
