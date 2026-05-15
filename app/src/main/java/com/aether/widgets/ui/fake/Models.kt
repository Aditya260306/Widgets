package com.aether.widgets.ui.fake

import androidx.compose.ui.graphics.Color

// ─── Enums ────────────────────────────────────────────────────────────────────

enum class WidgetType { WEATHER, INSIGHT, NEWS, HABIT, SYSTEM, CALENDAR }

enum class WidgetSize { COMPACT, SMALL, MEDIUM, LARGE }

enum class FreshnessState { FRESH, DEFAULT, STALE, ERROR }

enum class SourceStatus { LIVE, CACHED, ERROR }

enum class BuilderPhase { PROMPT, GENERATING, PREVIEW, SIZE, BEHAVIOUR, CONFIRM }

// ─── Sealed content ───────────────────────────────────────────────────────────

sealed class WidgetContent {
    data class WeatherContent(
        val temperatureC: Int,
        val condition: String,
        val feelsLikeC: Int,
        val highC: Int,
        val lowC: Int,
        val location: String,
        val forecast: List<ForecastDay> = emptyList()
    ) : WidgetContent()

    data class InsightContent(
        val insightText: String,
        val category: String,
        val accentColour: Color,
        val footnote: String
    ) : WidgetContent()

    data class NewsContent(
        val categoryTag: String,
        val headline: String,
        val sourceName: String,
        val timeAgo: String,
        val headlines: List<NewsHeadline> = emptyList()
    ) : WidgetContent()

    data class HabitContent(
        val habitName: String,
        val streakDays: Int,
        val completedToday: Boolean,
        val habits: List<HabitItem> = emptyList()
    ) : WidgetContent()

    data class SystemContent(
        val batteryPct: Int,
        val isCharging: Boolean,
        val storageUsedGb: Float,
        val storageTotalGb: Float,
        val errorMessage: String = ""
    ) : WidgetContent()

    data class CalendarContent(
        val eventTitle: String,
        val timeString: String,
        val platform: String
    ) : WidgetContent()

    object Skeleton : WidgetContent()
}

// ─── Supporting models ────────────────────────────────────────────────────────

data class WidgetModel(
    val id: String,
    val displayName: String,
    val type: WidgetType,
    val size: WidgetSize,
    val freshness: FreshnessState,
    val lastUpdatedMinutesAgo: Int,
    val accentColour: Color?,
    val aiCallsThisWeek: List<Int>,
    val cachedContent: WidgetContent
)

data class ContextSnapshot(
    val timeString: String,
    val dateString: String,
    val timeOfDayProfile: String,
    val locationCity: String,
    val locationDistrict: String,
    val locationPrecision: String,
    val batteryPct: Int,
    val isCharging: Boolean,
    val networkType: String,
    val networkBarsOf4: Int,
    val weatherTempC: Int,
    val weatherCondition: String,
    val weatherFeelsLikeC: Int,
    val nextEventTitle: String,
    val nextEventMinutesAway: Int,
    val nextEventPlatform: String,
    val focusActive: Boolean,
    val focusTimerLabel: String,
    val focusSuppressedWidgets: Int,
    val apiCallsUsed: Int,
    val apiCallsLimit: Int
)

data class ForecastDay(val day: String, val highC: Int, val lowC: Int, val icon: String)
data class NewsHeadline(val title: String, val source: String, val timeAgo: String)
data class HabitItem(val name: String, val streakDays: Int, val completedToday: Boolean, val accent: Color)
data class HistoryEntry(val timestampLabel: String, val contentSummary: String, val isCurrent: Boolean = false)
data class DataSource(val name: String, val status: SourceStatus, val lastResponseMs: Int)
data class WidgetOrchestrationRow(val widgetName: String, val status: String, val reason: String)

// ─── Builder state ────────────────────────────────────────────────────────────

data class BuilderState(
    val phase: BuilderPhase = BuilderPhase.PROMPT,
    val promptText: String = "",
    val previewWidget: WidgetModel? = null,
    val selectedSize: WidgetSize = WidgetSize.MEDIUM,
    val confidenceThreshold: Float = 0.7f
)
