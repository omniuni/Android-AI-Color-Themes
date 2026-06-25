package com.omniimpact.aicolorthemes.model

interface IDeepSeekQuery {
	val promptSystem: String
	val promptQuery: String
	fun copyWithPrompts(promptSystem: String, promptQuery: String): IDeepSeekQuery
}
