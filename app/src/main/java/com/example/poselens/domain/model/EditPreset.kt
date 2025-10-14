package com.example.poselens.domain.model

/**
 * Edit Preset for photo adjustments
 */
data class EditPreset(
    val id: String,
    val name: String,
    val sceneType: SceneType?,
    val adjustments: ImageAdjustments,
    val thumbnailUrl: String?,
    val isCustom: Boolean = false
) {
    companion object {
        // Predefined presets
        fun getDefaultPresets(): List<EditPreset> = listOf(
            EditPreset(
                id = "preset_golden_hour",
                name = "Golden Hour",
                sceneType = SceneType.LANDSCAPE_SUNSET,
                adjustments = ImageAdjustments(
                    brightness = 5f,
                    contrast = 10f,
                    saturation = 15f,
                    temperature = 20f,
                    shadows = 15f
                ),
                thumbnailUrl = null
            ),
            EditPreset(
                id = "preset_nature_fresh",
                name = "Nature Fresh",
                sceneType = SceneType.LANDSCAPE_NATURE,
                adjustments = ImageAdjustments(
                    brightness = 0f,
                    contrast = 15f,
                    saturation = 25f,
                    clarity = 20f
                ),
                thumbnailUrl = null
            ),
            EditPreset(
                id = "preset_indoor_cozy",
                name = "Indoor Cozy",
                sceneType = SceneType.PORTRAIT_SINGLE,
                adjustments = ImageAdjustments(
                    brightness = 10f,
                    temperature = 15f,
                    shadows = 20f
                ),
                thumbnailUrl = null
            ),
            EditPreset(
                id = "preset_urban_street",
                name = "Urban Street",
                sceneType = SceneType.STREET_PHOTOGRAPHY,
                adjustments = ImageAdjustments(
                    contrast = 20f,
                    saturation = -10f,
                    clarity = 15f
                ),
                thumbnailUrl = null
            ),
            EditPreset(
                id = "preset_vintage_film",
                name = "Vintage Film",
                sceneType = null,
                adjustments = ImageAdjustments(
                    saturation = -20f,
                    temperature = 10f,
                    grain = 30f,
                    vignette = 20f
                ),
                thumbnailUrl = null
            )
        )
    }
}

/**
 * Image Adjustments
 * All values range from -100 to 100 (except clarity, vignette, grain: 0 to 100)
 */
data class ImageAdjustments(
    val brightness: Float = 0f,     // -100 to 100
    val contrast: Float = 0f,       // -100 to 100
    val saturation: Float = 0f,     // -100 to 100
    val temperature: Float = 0f,    // -100 to 100 (cool to warm)
    val tint: Float = 0f,           // -100 to 100 (green to magenta)
    val shadows: Float = 0f,        // -100 to 100
    val highlights: Float = 0f,     // -100 to 100
    val whites: Float = 0f,         // -100 to 100
    val blacks: Float = 0f,         // -100 to 100
    val clarity: Float = 0f,        // 0 to 100
    val sharpness: Float = 0f,      // 0 to 100
    val vignette: Float = 0f,       // 0 to 100
    val grain: Float = 0f           // 0 to 100
) {
    /**
     * Check if any adjustments have been made
     */
    fun hasAdjustments(): Boolean {
        return brightness != 0f || contrast != 0f || saturation != 0f ||
                temperature != 0f || tint != 0f || shadows != 0f ||
                highlights != 0f || whites != 0f || blacks != 0f ||
                clarity != 0f || sharpness != 0f || vignette != 0f || grain != 0f
    }
    
    /**
     * Reset all adjustments to default
     */
    fun reset(): ImageAdjustments = ImageAdjustments()
}
