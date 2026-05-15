package com.aether.widgets.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aether.widgets.ui.fake.*
import com.aether.widgets.ui.theme.*

/**
 * WidgetCard — renders any WidgetModel in any FreshnessState.
 * All 6 content types + Skeleton shimmer are handled here.
 */
@Composable
fun WidgetCard(
    widget: WidgetModel,
    modifier: Modifier = Modifier,
    isInteractive: Boolean = true
) {
    val borderModifier = when (widget.freshness) {
        FreshnessState.FRESH -> Modifier.border(
            1.dp, AuraPrimary.copy(alpha = 0.7f), ShapeCard
        )
        FreshnessState.ERROR -> Modifier.border(
            1.dp, AuraRose.copy(alpha = 0.6f), ShapeCard
        )
        FreshnessState.STALE -> Modifier.border(
            1.dp, AuraOutlineVariant.copy(alpha = 0.3f), ShapeCard
        )
        else -> Modifier.ghostBorder(16)
    }

    val cardHeight = when (widget.size) {
        WidgetSize.COMPACT -> 80.dp
        WidgetSize.SMALL   -> 120.dp
        WidgetSize.MEDIUM  -> 160.dp
        WidgetSize.LARGE   -> 200.dp
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight)
            .clip(ShapeCard)
            .background(AuraSurface)
            .then(borderModifier)
            .padding(16.dp)
    ) {
        // Error dot
        if (widget.freshness == FreshnessState.ERROR) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(AuraRose)
                    .align(Alignment.TopEnd)
            )
        }

        // Stale overlay
        val contentAlpha = if (widget.freshness == FreshnessState.STALE) 0.6f else 1f

        when (val content = widget.cachedContent) {
            is WidgetContent.WeatherContent -> WeatherCardContent(content, contentAlpha, widget.accentColour)
            is WidgetContent.InsightContent -> InsightCardContent(content, contentAlpha, widget.accentColour)
            is WidgetContent.NewsContent    -> NewsCardContent(content, contentAlpha)
            is WidgetContent.HabitContent   -> HabitCardContent(content, contentAlpha, widget.accentColour)
            is WidgetContent.SystemContent  -> SystemCardContent(content, contentAlpha)
            is WidgetContent.CalendarContent -> CalendarCardContent(content, contentAlpha)
            WidgetContent.Skeleton          -> ShimmerBox(modifier = Modifier.fillMaxSize())
        }

        // Stale timestamp footer
        if (widget.freshness == FreshnessState.STALE) {
            Row(
                modifier = Modifier.align(Alignment.BottomStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.AccessTime, null,
                    tint = AuraOutline,
                    modifier = Modifier.size(10.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    "${widget.lastUpdatedMinutesAgo / 60}h ${widget.lastUpdatedMinutesAgo % 60}m ago",
                    style = MaterialTheme.typography.labelSmall,
                    color = AuraOutline,
                    fontSize = 9.sp
                )
            }
        }
    }
}

// ─── Weather card content ────────────────────────────────────────────────────

@Composable
private fun WeatherCardContent(
    c: WidgetContent.WeatherContent,
    alpha: Float,
    accent: Color?
) {
    val tint = accent ?: AuraMint
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    "${c.temperatureC}°",
                    style = MaterialTheme.typography.displaySmall,
                    color = AuraTextPrimary.copy(alpha = alpha)
                )
                Text(
                    c.condition,
                    style = MaterialTheme.typography.bodySmall,
                    color = AuraTextSecondary.copy(alpha = alpha)
                )
            }
            Icon(Icons.Default.Cloud, null, tint = tint.copy(alpha = alpha), modifier = Modifier.size(32.dp))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, null, tint = AuraOutline.copy(alpha = alpha), modifier = Modifier.size(12.dp))
            Spacer(Modifier.width(4.dp))
            Text(c.location.uppercase(), style = MaterialTheme.typography.labelSmall, color = AuraOutline.copy(alpha = alpha))
            Spacer(Modifier.width(12.dp))
            Text("H:${c.highC}° L:${c.lowC}°", style = MaterialTheme.typography.labelSmall, color = AuraOutline.copy(alpha = alpha))
        }
    }
}

// ─── Insight card content ────────────────────────────────────────────────────

@Composable
private fun InsightCardContent(
    c: WidgetContent.InsightContent,
    alpha: Float,
    accent: Color?
) {
    val tint = accent ?: c.accentColour
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(tint.copy(alpha = 0.15f * alpha))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(c.category, style = MaterialTheme.typography.labelSmall, color = tint.copy(alpha = alpha), fontSize = 9.sp)
            }
        }
        Text(
            c.insightText,
            style = MaterialTheme.typography.bodyMedium,
            color = AuraTextPrimary.copy(alpha = alpha),
            maxLines = 4,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            c.footnote,
            style = MaterialTheme.typography.labelSmall,
            color = AuraTextSecondary.copy(alpha = alpha * 0.7f),
            fontSize = 9.sp
        )
    }
}

// ─── News card content ───────────────────────────────────────────────────────

@Composable
private fun NewsCardContent(c: WidgetContent.NewsContent, alpha: Float) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(AuraTextSecondary.copy(alpha = alpha)))
            Spacer(Modifier.width(6.dp))
            Text(c.categoryTag, style = MaterialTheme.typography.labelSmall, color = AuraTextSecondary.copy(alpha = alpha))
        }
        Text(
            c.headline,
            style = MaterialTheme.typography.bodyMedium,
            color = AuraTextPrimary.copy(alpha = alpha),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(c.sourceName, style = MaterialTheme.typography.labelSmall, color = AuraOutline.copy(alpha = alpha))
            Text(c.timeAgo, style = MaterialTheme.typography.labelSmall, color = AuraOutline.copy(alpha = alpha))
        }
    }
}

// ─── Habit card content ──────────────────────────────────────────────────────

@Composable
private fun HabitCardContent(
    c: WidgetContent.HabitContent,
    alpha: Float,
    accent: Color?
) {
    val tint = accent ?: AuraPrimary
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text("${c.streakDays}", style = MaterialTheme.typography.titleLarge, color = tint.copy(alpha = alpha))
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(c.habitName, style = MaterialTheme.typography.bodyMedium, color = AuraTextPrimary.copy(alpha = alpha))
            Text(
                if (c.completedToday) "Completed today ✓" else "Not yet today",
                style = MaterialTheme.typography.labelSmall,
                color = if (c.completedToday) AuraMint.copy(alpha = alpha) else AuraOutline.copy(alpha = alpha)
            )
        }
    }
}

// ─── System card content ─────────────────────────────────────────────────────

@Composable
private fun SystemCardContent(c: WidgetContent.SystemContent, alpha: Float) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("BATTERY", style = MaterialTheme.typography.labelSmall, color = AuraOutline.copy(alpha = alpha))
            val batColor = if (c.batteryPct <= 20) AuraRose else AuraMint
            Text("${c.batteryPct}%", style = MaterialTheme.typography.titleLarge, color = batColor.copy(alpha = alpha))
        }
        if (c.errorMessage.isNotEmpty()) {
            Text(
                c.errorMessage,
                style = MaterialTheme.typography.labelSmall,
                color = AuraRose.copy(alpha = alpha),
                fontSize = 10.sp
            )
        }
    }
}

// ─── Calendar card content ───────────────────────────────────────────────────

@Composable
private fun CalendarCardContent(c: WidgetContent.CalendarContent, alpha: Float) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(c.eventTitle, style = MaterialTheme.typography.titleMedium, color = AuraTextPrimary.copy(alpha = alpha))
        Text(c.timeString, style = MaterialTheme.typography.bodySmall, color = AuraTextSecondary.copy(alpha = alpha))
        Text(c.platform, style = MaterialTheme.typography.labelSmall, color = AuraOutline.copy(alpha = alpha))
    }
}
