package com.aether.widgets.di

import android.content.Context
import androidx.room.Room
import com.aether.widgets.db.AetherDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AetherDatabase {
        return Room.databaseBuilder(
            context,
            AetherDatabase::class.java,
            "aether_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideWeatherApi(): com.aether.widgets.data.sources.WeatherApi {
        return retrofit2.Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
            .create(com.aether.widgets.data.sources.WeatherApi::class.java)
    }
}
