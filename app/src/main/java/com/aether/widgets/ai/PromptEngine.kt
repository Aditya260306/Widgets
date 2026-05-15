package com.aether.widgets.ai

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromptEngine @Inject constructor() {

    fun buildWeatherPrompt(weatherData: String, userInstructions: String?): String {
        val basePrompt = """
            You are an AI assistant for a personalized Android widget system called Aether.
            Summarize the following weather data in a concise, professional, and helpful way.
            Keep the summary under 20 words.
            
            Weather Data:
            $weatherData
        """.trimIndent()

        val instructionPrompt = if (!userInstructions.isNullOrBlank()) {
            "\n\nUser specific instructions: $userInstructions"
        } else {
            ""
        }

        return basePrompt + instructionPrompt
    }
}
