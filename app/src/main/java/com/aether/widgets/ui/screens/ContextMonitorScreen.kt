package com.aether.widgets.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aether.widgets.ui.components.*
import com.aether.widgets.ui.fake.*
import com.aether.widgets.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContextMonitorScreen(onBack: () -> Unit) {
    val ctx = FakeData.context

    Scaffold(
        containerColor = AuraBase,
        topBar = {
            TopAppBar(
                title = { Text("Context Engine", style = MaterialTheme.typography.headlineMedium, color = AuraTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = AuraTextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AuraBase.copy(alpha = 0.95f))
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
        ) {
            // ─── Live pulse indicator ─────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().clip(ShapeTile).background(AuraSurface).ghostBorder(12).padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AuraMint))
                    Spacer(Modifier.width(10.dp))
                    Text("Live Context · ${ctx.timeString}", style = MaterialTheme.typography.labelMedium, color = AuraMint)
                    Spacer(Modifier.weight(1f))
                    Text(ctx.dateString, style = MaterialTheme.typography.labelSmall, color = AuraOutline)
                }
            }

            // ─── Context snapshot cards ────────────────────────────────────────
            item { SectionHeader("CONTEXT SNAPSHOT") }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Location
                    ContextTile(
                        label = "LOCATION",
                        value = ctx.locationCity,
                        sub = ctx.locationDistrict,
                        icon = Icons.Default.LocationOn,
                        accent = AuraMint,
                        modifier = Modifier.weight(1f)
                    )
                    // Weather
                    ContextTile(
                        label = "WEATHER",
                        value = "${ctx.weatherTempC}°C",
                        sub = ctx.weatherCondition,
                        icon = Icons.Default.Cloud,
                        accent = AuraPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Battery
                    ContextTile(
                        label = "BATTERY",
                        value = "${ctx.batteryPct}%",
                        sub = if (ctx.isCharging) "Charging" else "On battery",
                        icon = if (ctx.isCharging) Icons.Default.BatteryChargingFull else Icons.Default.Battery5Bar,
                        accent = if (ctx.batteryPct < 20) AuraRose else AuraMint,
                        modifier = Modifier.weight(1f)
                    )
                    // Network
                    ContextTile(
                        label = "NETWORK",
                        value = ctx.networkType,
                        sub = "${ctx.networkBarsOf4}/4 bars",
                        icon = Icons.Default.Wifi,
                        accent = AuraPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ─── Next event ───────────────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().clip(ShapeTile).background(AuraSurface).ghostBorder(12).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Event, null, tint = AuraGold, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("NEXT EVENT", style = MaterialTheme.typography.labelSmall, color = AuraGold)
                    }
                    Text(ctx.nextEventTitle, style = MaterialTheme.typography.headlineSmall, color = AuraTextPrimary)
                    Text("In ${ctx.nextEventMinutesAway} min · ${ctx.nextEventPlatform}", style = MaterialTheme.typography.bodySmall, color = AuraTextSecondary)
                }
            }

            // ─── Focus mode ───────────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().clip(ShapeTile)
                        .background(if (ctx.focusActive) AuraPrimary.copy(alpha = 0.08f) else AuraSurface)
                        .border(1.dp, if (ctx.focusActive) AuraPrimary.copy(alpha = 0.4f) else AuraOutlineVariant.copy(alpha = 0.3f), ShapeTile)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DoNotDisturb, null, tint = if (ctx.focusActive) AuraPrimary else AuraOutline, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Focus Mode", style = MaterialTheme.typography.bodyMedium, color = if (ctx.focusActive) AuraPrimary else AuraTextPrimary)
                            if (ctx.focusActive) {
                                Text("${ctx.focusTimerLabel} · ${ctx.focusSuppressedWidgets} suppressed", style = MaterialTheme.typography.bodySmall, color = AuraTextSecondary)
                            }
                        }
                    }
                    if (ctx.focusActive) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AuraPrimary))
                    }
                }
            }

            // ─── API usage ────────────────────────────────────────────────────
            item { SectionHeader("API USAGE") }
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().clip(ShapeTile).background(AuraSurface).ghostBorder(12).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Gemini API calls today", style = MaterialTheme.typography.bodyMedium, color = AuraTextSecondary)
                        Text("${ctx.apiCallsUsed} / ${ctx.apiCallsLimit}", style = MaterialTheme.typography.headlineSmall, color = AuraTextPrimary)
                    }
                    LinearProgressIndicator(
                        progress = { ctx.apiCallsUsed.toFloat() / ctx.apiCallsLimit },
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(ShapeChip),
                        color = AuraPrimary,
                        trackColor = AuraSurfaceHighest
                    )
                    Text("${ctx.apiCallsLimit - ctx.apiCallsUsed} calls remaining", style = MaterialTheme.typography.labelSmall, color = AuraOutline)
                }
            }

            // ─── Widget orchestration ─────────────────────────────────────────
            item { SectionHeader("WIDGET ORCHESTRATION") }
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().clip(ShapeTile).background(AuraSurface).ghostBorder(12)
                ) {
                    FakeData.orchestrationRows.forEachIndexed { i, row ->
                        OrchestrationRow(row)
                        if (i < FakeData.orchestrationRows.lastIndex) {
                            Divider(color = AuraOutlineVariant.copy(alpha = 0.3f))
                        }
                    }
                }
            }

            // ─── Data sources ─────────────────────────────────────────────────
            item { SectionHeader("LIVE DATA SOURCES") }
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().clip(ShapeTile).background(AuraSurface).ghostBorder(12)
                ) {
                    val allSources = FakeData.dataSources.values.flatten().distinctBy { it.name }
                    allSources.forEachIndexed { i, src ->
                        DataSourceRow(src)
                        if (i < allSources.lastIndex) Divider(color = AuraOutlineVariant.copy(alpha = 0.3f))
                    }
                }
            }
        }
    }
}

@Composable
private fun ContextTile(
    label: String,
    value: String,
    sub: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clip(ShapeTile).background(AuraSurface).ghostBorder(12).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(6.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = accent, fontSize = 9.sp)
        }
        Text(value, style = MaterialTheme.typography.headlineSmall, color = AuraTextPrimary)
        Text(sub, style = MaterialTheme.typography.labelSmall, color = AuraTextSecondary, maxLines = 1)
    }
}

@Composable
private fun OrchestrationRow(row: WidgetOrchestrationRow) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(row.widgetName, style = MaterialTheme.typography.bodyMedium, color = AuraTextPrimary, modifier = Modifier.weight(1f))
        val statusColor = if (row.status == "Active") AuraMint else AuraGold
        Box(
            modifier = Modifier.clip(ShapeChip).background(statusColor.copy(alpha = 0.12f)).padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(row.status.uppercase(), style = MaterialTheme.typography.labelSmall, color = statusColor, fontSize = 9.sp)
        }
        if (row.reason.isNotBlank() && row.reason != "—") {
            Spacer(Modifier.width(8.dp))
            Text(row.reason, style = MaterialTheme.typography.labelSmall, color = AuraOutline, fontSize = 9.sp)
        }
    }
}

@Composable
private fun DataSourceRow(src: DataSource) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val dotColor = when (src.status) {
                SourceStatus.LIVE   -> AuraMint
                SourceStatus.CACHED -> AuraGold
                SourceStatus.ERROR  -> AuraRose
            }
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(dotColor))
            Spacer(Modifier.width(10.dp))
            Text(src.name, style = MaterialTheme.typography.bodySmall, color = AuraTextPrimary)
        }
        if (src.lastResponseMs > 0) {
            Text("${src.lastResponseMs}ms", style = MaterialTheme.typography.labelSmall, color = AuraOutline, fontSize = 9.sp)
        }
    }
}
