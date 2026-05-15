package com.aether.widgets.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aether.widgets.ai.GeminiRepository
import com.aether.widgets.ai.PromptEngine
import com.aether.widgets.db.AetherDatabase
import com.aether.widgets.db.WidgetEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RefreshWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val db: AetherDatabase,
    private val geminiRepository: GeminiRepository,
    private val promptEngine: PromptEngine,
    private val weatherRepository: com.aether.widgets.data.sources.WeatherRepository,
    private val keyManager: com.aether.widgets.data.KeyManager
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val widgetId = inputData.getInt("widgetId", 1)
        
        val weatherApiKey = keyManager.getWeatherKey() ?: return Result.failure()
        val geminiApiKey = keyManager.getGeminiKey() ?: return Result.failure()
        
        geminiRepository.setApiKey(geminiApiKey)
        
        // Mocking location for now (Bangalore)
        val lat = 12.9716 
        val lon = 77.5946
        
        val weatherData = weatherRepository.getCurrentWeather(lat, lon, weatherApiKey) 
            ?: "Weather data currently unavailable."
        
        val prompt = promptEngine.buildWeatherPrompt(weatherData, "Be poetic and brief.")
        
        val summary = geminiRepository.generateSummary(prompt) ?: "AI summary unavailable."

        val entity = WidgetEntity(
            widgetId = widgetId,
            type = "WEATHER",
            content = summary,
            timestamp = System.currentTimeMillis()
        )

        db.widgetDao().insertWidget(entity)

        return Result.success()
    }
}
