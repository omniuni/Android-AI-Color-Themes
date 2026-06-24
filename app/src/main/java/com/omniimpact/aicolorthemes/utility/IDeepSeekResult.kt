package com.omniimpact.aicolorthemes.utility

interface IDeepSeekResult<T> {
    fun onFailure(message: String)
    fun onSuccess(result: T)
}