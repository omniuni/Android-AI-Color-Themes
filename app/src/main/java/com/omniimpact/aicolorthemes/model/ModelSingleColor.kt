package com.omniimpact.aicolorthemes.model

data class ModelSingleColor(
    override val promptSystem: String = """Return a single Hex color.
		Prefer bold or saturated colors.
		""".trimMargin(),
	override val promptQuery: String = "",
	val colorHex: String = ""
) : IDeepSeekQuery {
	override fun copyWithPrompts(promptSystem: String, promptQuery: String): ModelSingleColor =
		copy(promptSystem = promptSystem, promptQuery = promptQuery)
}