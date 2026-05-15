package com.aether.widgets.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiRepository @Inject constructor() {
    
    // In a real app, the API key would be securely retrieved (e.g., from Keystore or BuildConfig)
    private var apiKey: String? = null

    fun setApiKey(key: String) {
        apiKey = key
    }

    suspend fun generateSummary(prompt: String): String? {
        val currentKey = apiKey ?: return "Error: No API Key provided"
        
        val generativeModel = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = currentKey
        )

        return try {
            val response = generativeModel.generateContent(prompt)
            response.text
        } catch (e: Exception) {
            "Error: ${e.localizedMessage}"
        }
    }
}
