package com.omniimpact.aicolorthemes.model

data class ModelColorTheme(
    override val promptSystem: String = """Return a color theme between 3 and 12 colors.
        Make sure to include at least one very dark and one very light color for contrast.
        Include at least one complimentary color with a distinctly different hue or shade from the others.
        In larger themes, include some colors of different saturation.
        Do not repeat colors. Do not include hex codes in the name or description of the theme.
        Order the colors from darker shades to lighter, except the anchor color, which is always first.
        Most of the theme should be based on the anchor color if it is provided.
        Try to vary the approach to the color theme within these parameters, and avoid hues that are too similar and could clash.
        If the user requests `with` a color, or an `accent`, or similar phrasing, include it only as necessary.
        If an anchor color is provided, always make it the first one in the theme regardless of shade or brightness.
        """.trimMargin(),
    override val promptQuery: String,
    val themeName: String,
    val themeDescription: String,
    val colorTheme: List<String>
) : IDeepSeekQuery