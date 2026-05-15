package com.aether.widgets.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aether.widgets.ui.components.*
import com.aether.widgets.ui.fake.*
import com.aether.widgets.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToBuilder: () -> Unit,
    onNavigateToContext: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToFidgetSpace: () -> Unit,
    onNavigateToDetail: (String) -> Unit = {}
) {
    val widgets = remember { mutableStateListOf(*FakeData.widgets.toTypedArray()) }
    var showRemoveSheet by remember { mutableStateOf<String?>(null) }
    var showCardSheet by remember { mutableStateOf<String?>(null) }
    var showEmptyState by remember { mutableStateOf(FakeData.showEmptyState) }

    // Swipe-up to fidget detector
    var dragStartY by remember { mutableStateOf(0f) }

    Scaffold(
        containerColor = AuraBase,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToBuilder,
                shape = CircleShape,
                containerColor = AuraPrimary,
                contentColor = AuraTextPrimary,
                elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Add, "Create Widget", modifier = Modifier.size(24.dp))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset -> dragStartY = offset.y },
                        onDragEnd = {},
                        onDrag = { change, dragAmount ->
                            change.consume()
                            if (dragAmount.y < -60f) {
                                onNavigateToFidgetSpace()
                            }
                        }
                    )
                }
        ) {
            // Status Strip — long-press triggers Context Monitor
            Box(
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(onLongPress = { onNavigateToContext() })
                }
            ) {
                StatusStrip(
                    apiUsed = FakeData.context.apiCallsUsed,
                    apiLimit = FakeData.context.apiCallsLimit,
                    onGearClick = onNavigateToSettings,
                    onLongPress = onNavigateToContext
                )
            }

            if (showEmptyState || widgets.isEmpty()) {
                DashboardEmptyState(onAdd = onNavigateToBuilder)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 88.dp, top = 8.dp)
                ) {
                    items(widgets, key = { it.id }) { widget ->
                        var offsetX by remember { mutableStateOf(0f) }
                        var isRefreshing by remember { mutableStateOf(false) }
                        val animOffset by animateFloatAsState(targetValue = offsetX, label = "swipe_${widget.id}")

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .pointerInput(widget.id) {
                                    detectDragGestures(
                                        onDragEnd = {
                                            when {
                                                offsetX > 100f -> {
                                                    // Swipe right = refresh
                                                    offsetX = 0f
                                                    isRefreshing = true
                                                }
                                                offsetX < -100f -> {
                                                    // Swipe left = remove confirmation
                                                    offsetX = 0f
                                                    showRemoveSheet = widget.id
                                                }
                                                else -> offsetX = 0f
                                            }
                                        },
                                        onDrag = { change, amount ->
                                            change.consume()
                                            offsetX += amount.x
                                        }
                                    )
                                }
                                .pointerInput(widget.id) {
                                    detectTapGestures(
                                        onTap = { onNavigateToDetail(widget.id) },
                                        onLongPress = { showCardSheet = widget.id }
                                    )
                                }
                        ) {
                            // Swipe-right refresh reveal
                            if (offsetX > 0) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .align(Alignment.CenterStart)
                                        .padding(horizontal = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Refresh, null, tint = AuraMint, modifier = Modifier.size(24.dp))
                                }
                            }
                            // Swipe-left remove reveal
                            if (offsetX < 0) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .align(Alignment.CenterEnd)
                                        .padding(horizontal = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Delete, null, tint = AuraRose, modifier = Modifier.size(24.dp))
                                }
                            }

                            Box(modifier = Modifier.offset(x = (animOffset * 0.3f).dp)) {
                                if (isRefreshing) {
                                    LaunchedEffect(Unit) {
                                        delay(1500)
                                        isRefreshing = false
                                    }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(160.dp)
                                            .clip(ShapeCard)
                                            .background(AuraSurface)
                                            .ghostBorder(16),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            color = AuraPrimary,
                                            modifier = Modifier.size(24.dp),
                                            strokeWidth = 2.dp
                                        )
                                    }
                                } else {
                                    WidgetCard(widget = widget, modifier = Modifier.fillMaxWidth())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Remove confirmation sheet
    if (showRemoveSheet != null) {
        val widgetId = showRemoveSheet!!
        ConfirmSheet(
            title = "Remove widget?",
            body = "This widget will be removed from your dashboard. Your settings will be saved.",
            confirmLabel = "Remove",
            onConfirm = {
                widgets.removeAll { it.id == widgetId }
                if (widgets.isEmpty()) showEmptyState = true
            },
            onDismiss = { showRemoveSheet = null }
        )
    }

    // Card long-press action sheet
    if (showCardSheet != null) {
        val widgetId = showCardSheet!!
        val widget = widgets.find { it.id == widgetId }
        if (widget != null) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            AuraBottomSheet(
                onDismiss = { showCardSheet = null },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(widget.displayName, style = MaterialTheme.typography.headlineSmall, color = AuraTextPrimary)
                    Spacer(Modifier.height(8.dp))
                    CardSheetAction(Icons.Default.Refresh, "Refresh now") { showCardSheet = null }
                    CardSheetAction(Icons.Default.Edit, "Edit widget") { onNavigateToBuilder(); showCardSheet = null }
                    CardSheetAction(Icons.Default.OpenInNew, "View detail") { onNavigateToDetail(widgetId); showCardSheet = null }
                    CardSheetAction(Icons.Default.Delete, "Remove", color = AuraRose) {
                        widgets.removeAll { it.id == widgetId }
                        showCardSheet = null
                    }
                }
            }
        }
    }
}

@Composable
private fun CardSheetAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color = AuraTextPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ShapeTile)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(16.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, color = color)
    }
}

@Composable
private fun DashboardEmptyState(onAdd: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Dashed border silhouette
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(ShapeCard)
                .background(AuraPrimary.copy(alpha = 0.05f * alpha))
                .border(1.dp, AuraPrimary.copy(alpha = alpha), ShapeCard),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.AddBox, null, tint = AuraPrimary.copy(alpha = alpha), modifier = Modifier.size(32.dp))
                Spacer(Modifier.height(8.dp))
                Text("Your first widget is waiting", style = MaterialTheme.typography.bodyMedium, color = AuraTextSecondary.copy(alpha = alpha))
            }
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onAdd,
            colors = ButtonDefaults.buttonColors(containerColor = AuraPrimary),
            shape = ShapeButton
        ) {
            Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Create Widget", style = MaterialTheme.typography.labelLarge, color = AuraTextPrimary)
        }
    }
}
