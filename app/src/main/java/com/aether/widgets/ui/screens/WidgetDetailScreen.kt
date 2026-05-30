package com.aether.widgets.ui.screens

import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aether.widgets.ui.components.*
import com.aether.widgets.ui.fake.*
import com.aether.widgets.ui.theme.*
import com.aether.widgets.ui.utils.hapticClickable
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetDetailScreen(
    onBack: () -> Unit,
    widgetId: String = "w1",
    onNavigateToBuilder: () -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    val widget = FakeData.widgets.find { it.id == widgetId } ?: FakeData.widgets.first()
    val history = FakeData.widgetHistory[widgetId] ?: emptyList()
    val sources = FakeData.dataSources[widgetId] ?: emptyList()
    var isRefreshing by remember { mutableStateOf(false) }
    var selectedHistoryEntry by remember { mutableStateOf<HistoryEntry?>(null) }

    Scaffold(
        containerColor = AuraBase,
        topBar = {
            TopAppBar(
                title = { Text(widget.displayName, style = MaterialTheme.typography.headlineMedium, color = AuraTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = AuraTextSecondary)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onNavigateToBuilder()
                    }) {
                        Icon(Icons.Default.Edit, null, tint = AuraTextSecondary)
                    }
                    IconButton(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        if (!isRefreshing) isRefreshing = true
                    }) {
                        if (isRefreshing) {
                            LaunchedEffect(Unit) { delay(2000); isRefreshing = false }
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = AuraPrimary, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Refresh, null, tint = AuraTextSecondary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AuraBase.copy(alpha = 0.95f))
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 88.dp)
        ) {
            // Hero widget
            item {
                WidgetCard(widget = widget, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(4.dp))
                Text(
                    "Last updated ${widget.lastUpdatedMinutesAgo} min ago",
                    style = MaterialTheme.typography.labelSmall,
                    color = AuraOutline,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // Metadata panel
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(ShapeTile)
                        .background(AuraSurface)
                        .ghostBorder(12),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    // Type chip
                    DetailMetaRow("Widget Type") {
                        Box(
                            modifier = Modifier
                                .clip(ShapeChip)
                                .background(AuraPrimary.copy(alpha = 0.1f))
                                .border(1.dp, AuraPrimary.copy(alpha = 0.3f), ShapeChip)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(widget.type.name, style = MaterialTheme.typography.labelSmall, color = AuraPrimary, fontSize = 10.sp)
                        }
                    }

                    Divider(color = AuraOutlineVariant.copy(alpha = 0.3f))

                    // Data sources
                    DetailMetaRow("Data Sources") {
                        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            sources.forEach { src ->
                                val dotColor = when (src.status) {
                                    SourceStatus.LIVE   -> AuraMint
                                    SourceStatus.CACHED -> AuraGold
                                    SourceStatus.ERROR  -> AuraRose
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(dotColor))
                                    Spacer(Modifier.width(6.dp))
                                    Text(src.name, style = MaterialTheme.typography.bodySmall, color = AuraTextSecondary, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    Divider(color = AuraOutlineVariant.copy(alpha = 0.3f))
                    DetailMetaRow("Refresh") { Text("Hourly", style = MaterialTheme.typography.bodySmall, color = AuraTextPrimary) }

                    Divider(color = AuraOutlineVariant.copy(alpha = 0.3f))

                    // AI calls sparkline
                    DetailMetaRow("AI Calls (7d)") {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Sparkline(values = widget.aiCallsThisWeek, barColor = AuraPrimary)
                            Spacer(Modifier.width(8.dp))
                            Text("${widget.aiCallsThisWeek.sum()}", style = MaterialTheme.typography.bodySmall, color = AuraTextPrimary)
                        }
                    }

                    Divider(color = AuraOutlineVariant.copy(alpha = 0.3f))
                    DetailMetaRow("Offline Mode") {
                        Box(
                            modifier = Modifier.clip(ShapeChip).background(AuraSurfaceHigh)
                                .border(1.dp, AuraOutlineVariant, ShapeChip).padding(horizontal = 10.dp, vertical = 4.dp)
                        ) { Text("Last Known", style = MaterialTheme.typography.labelSmall, color = AuraTextSecondary) }
                    }
                }
            }

            // Content history
            item {
                SectionHeader("CONTENT HISTORY")
            }
            items(history.size) { i ->
                val entry = history[i]
                val isCurrent = entry.isCurrent
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(ShapeTile)
                        .background(if (isCurrent) AuraPrimary.copy(alpha = 0.05f) else AuraSurface)
                        .border(1.dp, if (isCurrent) AuraPrimary.copy(alpha = 0.3f) else AuraOutlineVariant.copy(alpha = 0.3f), ShapeTile)
                        .hapticClickable { selectedHistoryEntry = entry }
                        .padding(16.dp)
                ) {
                    if (isCurrent) {
                        Box(modifier = Modifier.width(3.dp).height(40.dp).clip(ShapeChip).background(AuraPrimary).align(Alignment.TopStart))
                    }
                    Column(modifier = Modifier.padding(start = if (isCurrent) 10.dp else 0.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(entry.timestampLabel, style = MaterialTheme.typography.labelMedium, color = AuraOutline)
                            if (isCurrent) {
                                Box(modifier = Modifier.clip(ShapeChip).background(AuraPrimary.copy(alpha = 0.15f)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                    Text("CURRENT", style = MaterialTheme.typography.labelSmall, color = AuraPrimary, fontSize = 9.sp)
                                }
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(entry.contentSummary, style = MaterialTheme.typography.bodySmall, color = AuraTextPrimary, maxLines = 2)
                    }
                }
            }
        }
    }

    // History entry full-content bottom sheet
    if (selectedHistoryEntry != null) {
        val entry = selectedHistoryEntry!!
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        AuraBottomSheet(onDismiss = { selectedHistoryEntry = null }, sheetState = sheetState) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(entry.timestampLabel, style = MaterialTheme.typography.labelMedium, color = AuraOutline)
                Text(entry.contentSummary, style = MaterialTheme.typography.bodyLarge, color = AuraTextPrimary)
            }
        }
    }
}

@Composable
private fun DetailMetaRow(label: String, content: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(AuraSurface).padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = AuraTextSecondary)
        content()
    }
}

// Legacy stubs so MainActivity compiles
@Composable
fun HourlyForecast(time: String, icon: androidx.compose.ui.graphics.vector.ImageVector, iconTint: Color, temp: String) {}
@Composable
fun MetadataRow(title: String, content: @Composable () -> Unit) {}
@Composable
fun HistoryItem(time: String, status: String, content: String, isExpanded: Boolean = false) {}
fun androidx.compose.ui.Modifier.drawBehindTopBorder(color: Color) = this
fun androidx.compose.ui.Modifier.border(top: androidx.compose.ui.unit.Dp, color: Color) = this
