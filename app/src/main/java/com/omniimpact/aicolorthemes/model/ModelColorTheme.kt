package com.omniimpact.aicolorthemes.model

data class ModelColorTheme(
    override val promptSystem: String = "Return a color theme between 3 and 12 colors. Make sure to include at least one very dark and one very light color for contrast, and at least one complimentary color. If an anchor color is provided, this color must be the first one in the theme. This query returns JSON.",
    override val promptQuery: String,
    val themeName: String,
    val themeDescription: String,
    val colorTheme: List<String>
) : IDeepSeekQuery