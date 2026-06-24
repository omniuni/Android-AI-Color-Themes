package com.omniimpact.aicolorthemes.model

data class ModelSingleColor(
    override val promptSystem: String = "Return a single Hex color. Prefer bold or saturated colors. This query returns JSON.",
    override val promptQuery: String,
    val colorHex: String
) : IDeepSeekQuery