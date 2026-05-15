package com.aether.widgets.ui.fake

import androidx.compose.ui.graphics.Color

/**
 * FakeData — AURA Phase 1 Single Source of Truth
 *
 * All hardcoded data lives here. No data is ever hardcoded inside a Composable.
 * Phase 2 will wire real repositories one field at a time.
 */
object FakeData {

    // ─── Flag: toggle to show empty dashboard state ───────────────────────────
    val showEmptyState = false

    // ─── Accent color constants ───────────────────────────────────────────────
    private val Violet = Color(0xFF7C5CFC)
    private val Gold   = Color(0xFFF4C542)
    private val Mint   = Color(0xFF00D4A8)
    private val Rose   = Color(0xFFFF5F87)

    // ─── Widget list (5 widgets matching Handoff §2.2) ───────────────────────
    val widgets = listOf(

        // W1 — Morning Weather (FRESH)
        WidgetModel(
            id = "w1",
            displayName = "Morning Weather",
            type = WidgetType.WEATHER,
            size = WidgetSize.MEDIUM,
            freshness = FreshnessState.FRESH,
            lastUpdatedMinutesAgo = 3,
            accentColour = Mint,
            aiCallsThisWeek = listOf(2, 5, 3, 7, 4, 6, 3),
            cachedContent = WidgetContent.WeatherContent(
                temperatureC = 28,
                condition = "Partly Cloudy",
                feelsLikeC = 30,
                highC = 32,
                lowC = 22,
                location = "Delhi, IN",
                forecast = listOf(
                    ForecastDay("MON", 32, 22, "⛅"),
                    ForecastDay("TUE", 30, 21, "🌤"),
                    ForecastDay("WED", 35, 25, "☀️"),
                    ForecastDay("THU", 29, 23, "🌦"),
                    ForecastDay("FRI", 27, 20, "🌧")
                )
            )
        ),

        // W2 — Daily Insight (DEFAULT, Gold accent)
        WidgetModel(
            id = "w2",
            displayName = "Daily Insight",
            type = WidgetType.INSIGHT,
            size = WidgetSize.LARGE,
            freshness = FreshnessState.DEFAULT,
            lastUpdatedMinutesAgo = 47,
            accentColour = Gold,
            aiCallsThisWeek = listOf(1, 3, 2, 5, 4, 6, 8),
            cachedContent = WidgetContent.InsightContent(
                insightText = "Your focus sessions are 34% longer on days when you skip social media before 10 AM.",
                category = "BEHAVIOUR PATTERN",
                accentColour = Gold,
                footnote = "Based on 14 days of data"
            )
        ),

        // W3 — Top News (STALE — 3.5 hours old)
        WidgetModel(
            id = "w3",
            displayName = "Top News",
            type = WidgetType.NEWS,
            size = WidgetSize.MEDIUM,
            freshness = FreshnessState.STALE,
            lastUpdatedMinutesAgo = 210,
            accentColour = null,
            aiCallsThisWeek = listOf(4, 2, 6, 3, 5, 1, 4),
            cachedContent = WidgetContent.NewsContent(
                categoryTag = "TECH",
                headline = "AI Regulation updates: Global summit outlines new transparency mandates for deep learning models.",
                sourceName = "Reuters",
                timeAgo = "3h 30m ago",
                headlines = listOf(
                    NewsHeadline("AI Regulation updates: Global summit outlines new transparency mandates.", "Reuters", "3h"),
                    NewsHeadline("OpenAI announces GPT-5 with unprecedented reasoning capabilities.", "The Verge", "5h"),
                    NewsHeadline("India's startup ecosystem reaches record valuation milestone.", "Bloomberg", "7h")
                )
            )
        ),

        // W4 — My Habits (COMPACT)
        WidgetModel(
            id = "w4",
            displayName = "My Habits",
            type = WidgetType.HABIT,
            size = WidgetSize.COMPACT,
            freshness = FreshnessState.DEFAULT,
            lastUpdatedMinutesAgo = 12,
            accentColour = Violet,
            aiCallsThisWeek = listOf(0, 1, 0, 2, 1, 0, 1),
            cachedContent = WidgetContent.HabitContent(
                habitName = "Morning Meditation",
                streakDays = 12,
                completedToday = true,
                habits = listOf(
                    HabitItem("Morning Meditation", 12, true, Violet),
                    HabitItem("Daily Reading", 7, true, Gold),
                    HabitItem("Evening Run", 3, false, Mint),
                    HabitItem("No Phone Before 10AM", 5, true, Rose)
                )
            )
        ),

        // W5 — System (ERROR — battery 14%, refresh failed)
        WidgetModel(
            id = "w5",
            displayName = "System",
            type = WidgetType.SYSTEM,
            size = WidgetSize.SMALL,
            freshness = FreshnessState.ERROR,
            lastUpdatedMinutesAgo = 83,
            accentColour = Rose,
            aiCallsThisWeek = listOf(0, 0, 1, 0, 0, 1, 0),
            cachedContent = WidgetContent.SystemContent(
                batteryPct = 14,
                isCharging = false,
                storageUsedGb = 87.4f,
                storageTotalGb = 128f,
                errorMessage = "Refresh failed — no network"
            )
        )
    )

    // ─── Empty state list (used when showEmptyState = true) ──────────────────
    val emptyWidgetList: List<WidgetModel> = emptyList()

    // ─── Context snapshot ────────────────────────────────────────────────────
    val context = ContextSnapshot(
        timeString = "09:42",
        dateString = "Thursday, 15 May",
        timeOfDayProfile = "MORNING",
        locationCity = "Delhi",
        locationDistrict = "Gharoli",
        locationPrecision = "PRECISE",
        batteryPct = 78,
        isCharging = true,
        networkType = "WiFi",
        networkBarsOf4 = 3,
        weatherTempC = 28,
        weatherCondition = "Partly Cloudy",
        weatherFeelsLikeC = 30,
        nextEventTitle = "Team Standup",
        nextEventMinutesAway = 18,
        nextEventPlatform = "Google Meet",
        focusActive = true,
        focusTimerLabel = "01:24:00",
        focusSuppressedWidgets = 2,
        apiCallsUsed = 47,
        apiCallsLimit = 200
    )

    // ─── Widget history entries (shown in WidgetDetail screen) ───────────────
    val widgetHistory: Map<String, List<HistoryEntry>> = mapOf(
        "w1" to listOf(
            HistoryEntry("09:40 AM", "28°C, Partly Cloudy — Feels like 30°C", isCurrent = true),
            HistoryEntry("08:40 AM", "26°C, Clear — Feels like 27°C"),
            HistoryEntry("07:40 AM", "24°C, Haze — Feels like 25°C"),
            HistoryEntry("Yesterday 6PM", "31°C, Hot — Feels like 34°C"),
            HistoryEntry("Yesterday 12PM", "33°C, Sunny — Feels like 36°C")
        ),
        "w2" to listOf(
            HistoryEntry("09:00 AM", "Your focus sessions are 34% longer on days when you skip social media before 10 AM.", isCurrent = true),
            HistoryEntry("Yesterday 9AM", "You complete 2.3× more deep tasks on mornings after 7+ hours sleep."),
            HistoryEntry("Mon 9AM", "Meetings before 11 AM reduce your afternoon output by an average of 28%."),
            HistoryEntry("Sun 9AM", "Your most creative work happens between 2 PM and 4 PM.")
        )
    )

    // ─── Data sources per widget type ────────────────────────────────────────
    val dataSources: Map<String, List<DataSource>> = mapOf(
        "w1" to listOf(
            DataSource("Open-Meteo API", SourceStatus.LIVE, 120),
            DataSource("Device GPS", SourceStatus.LIVE, 0),
            DataSource("Gemini 1.5 Flash", SourceStatus.LIVE, 340)
        ),
        "w2" to listOf(
            DataSource("Gemini 1.5 Pro", SourceStatus.LIVE, 1200),
            DataSource("Calendar API", SourceStatus.CACHED, 0),
            DataSource("Screen Time API", SourceStatus.CACHED, 0)
        ),
        "w3" to listOf(
            DataSource("News API", SourceStatus.ERROR, 0),
            DataSource("Gemini 1.5 Flash", SourceStatus.CACHED, 0)
        ),
        "w4" to listOf(
            DataSource("Health Connect", SourceStatus.LIVE, 50)
        ),
        "w5" to listOf(
            DataSource("System API", SourceStatus.ERROR, 0)
        )
    )

    // ─── Widget orchestration rows (shown in Context Monitor) ─────────────────
    val orchestrationRows = listOf(
        WidgetOrchestrationRow("Morning Weather", "Active", "—"),
        WidgetOrchestrationRow("News Brief", "Suppressed", "Focus Mode"),
        WidgetOrchestrationRow("Commute Alert", "Active", "—")
    )

    // ─── Prompt placeholders (cycle every 3s in Builder) ─────────────────────
    val promptPlaceholders = listOf(
        "What would you like AURA to surface?",
        "Show my next meeting and weather together...",
        "Track my daily habit streaks...",
        "Summarise the morning news in 2 sentences...",
        "Monitor my battery and network status..."
    )

    // ─── Quick-start chips (Builder - Phase A) ────────────────────────────────
    val quickStartChips = listOf(
        "Morning Brief",
        "Weather + Calendar",
        "Habit Tracker",
        "News Summary",
        "System Status"
    )
}
