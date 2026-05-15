package com.aether.widgets.data.sources

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val weatherApi: WeatherApi
) {
    suspend fun getCurrentWeather(lat: Double, lon: Double, apiKey: String): String? {
        return try {
            val response = weatherApi.getCurrentWeather(lat, lon, apiKey)
            if (response.isSuccessful) {
                val body = response.body()
                "Weather in ${body?.name}: ${body?.weather?.firstOrNull()?.description}, Temp: ${body?.main?.temp}°C"
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}

interface WeatherApi {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Response<WeatherResponse>
}

data class WeatherResponse(
    @SerializedName("name") val name: String,
    @SerializedName("weather") val weather: List<WeatherDescription>,
    @SerializedName("main") val main: MainData
)

data class WeatherDescription(
    @SerializedName("description") val description: String
)

data class MainData(
    @SerializedName("temp") val temp: Double
)
