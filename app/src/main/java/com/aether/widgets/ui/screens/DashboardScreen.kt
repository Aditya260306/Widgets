package com.aether.widgets.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.aether.widgets.ui.components.*
import com.aether.widgets.ui.fake.*
import com.aether.widgets.ui.theme.*
import com.aether.widgets.ui.utils.hapticClickable
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
    val haptic = LocalHapticFeedback.current
    val widgets = remember { mutableStateListOf(*FakeData.widgets.toTypedArray()) }
    var showRemoveSheet by remember { mutableStateOf<String?>(null) }
    var showCardSheet by remember { mutableStateOf<String?>(null) }
    var showEmptyState by remember { mutableStateOf(FakeData.showEmptyState) }

    // Velocity tracking for cards
    val lazyListState = rememberLazyListState()
    var lastOffset by remember { mutableStateOf(0) }
    var lastIndex by remember { mutableStateOf(0) }
    var velocity by remember { mutableStateOf(0f) }
    
    LaunchedEffect(lazyListState) {
        snapshotFlow { 
            lazyListState.firstVisibleItemScrollOffset to lazyListState.firstVisibleItemIndex 
        }.collect { (currentOffset, currentIndex) ->
            // Simple velocity estimation: change in pixels since last frame/update
            val delta = if (currentIndex == lastIndex) {
                (currentOffset - lastOffset).toFloat()
            } else {
                // Index changed, calculate approximate jump (assuming average item height)
                if (currentIndex > lastIndex) 500f else -500f
            }
            
            velocity = (velocity * 0.7f) + (delta * 0.3f)
            lastOffset = currentOffset
            lastIndex = currentIndex
        }
    }
    
    // Auto-decay velocity when not scrolling
    if (velocity != 0f && !lazyListState.isScrollInProgress) {
        LaunchedEffect(Unit) {
            while (velocity != 0f) {
                delay(16)
                velocity *= 0.8f
                if (Math.abs(velocity) < 0.1f) velocity = 0f
            }
        }
    }

    // Rubber-band overscroll logic
    var overscrollOffset by remember { mutableStateOf(0f) }
    val animOverscroll by animateFloatAsState(
        targetValue = overscrollOffset,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow),
        label = "overscroll"
    )
    
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // If we're currently in an overscroll state, consume some of the scroll to return to neutral
                if (overscrollOffset > 0 && available.y < 0) {
                    val consumed = available.y.coerceAtLeast(-overscrollOffset)
                    overscrollOffset += consumed
                    return Offset(0f, consumed)
                }
                if (overscrollOffset < 0 && available.y > 0) {
                    val consumed = available.y.coerceAtMost(-overscrollOffset)
                    overscrollOffset += consumed
                    return Offset(0f, consumed)
                }
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // When we hit the bounds, start accumulating overscroll
                if (available.y != 0f) {
                    overscrollOffset += available.y * 0.4f // Resist the pull
                }
                return Offset.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                // Return to zero when finger is lifted
                overscrollOffset = 0f
                return Velocity.Zero
            }
        }
    }

    // Swipe-up to fidget detector
    var dragStartY by remember { mutableStateOf(0f) }

    // Blur state for sheets
    val blurValue by animateDpAsState(
        targetValue = if (showCardSheet != null || showRemoveSheet != null) 16.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "contentBlur"
    )
    
    val currentScrollOffset by remember { derivedStateOf { lazyListState.firstVisibleItemScrollOffset } }

    Scaffold(
        modifier = Modifier
            .nestedScroll(nestedScrollConnection)
            .blur(blurValue),
        containerColor = AuraBase,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onNavigateToBuilder()
                },
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
                modifier = Modifier
                    .graphicsLayer {
                        // Subtle parallax/tilt for the header
                        translationY = -lazyListState.firstVisibleItemScrollOffset.toFloat() * 0.2f
                        alpha = (1f - (lazyListState.firstVisibleItemScrollOffset / 200f)).coerceIn(0f, 1f)
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(onLongPress = { 
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onNavigateToContext() 
                        })
                    }
            ) {
                StatusStrip(
                    apiUsed = FakeData.context.apiCallsUsed,
                    apiLimit = FakeData.context.apiCallsLimit,
                    onGearClick = onNavigateToSettings,
                    onLongPress = onNavigateToContext,
                    scrollOffset = currentScrollOffset
                )
            }

            if (showEmptyState || widgets.isEmpty()) {
                DashboardEmptyState(onAdd = onNavigateToBuilder)
            } else {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .graphicsLayer {
                            // Rubber-band scaling and translation
                            val overscrollScale = 1f + (Math.abs(animOverscroll) / 5000f)
                            scaleX = overscrollScale
                            scaleY = overscrollScale
                            translationY = animOverscroll
                            transformOrigin = if (animOverscroll > 0) {
                                androidx.compose.ui.graphics.TransformOrigin(0.5f, 0f)
                            } else {
                                androidx.compose.ui.graphics.TransformOrigin(0.5f, 1f)
                            }
                        },
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 88.dp, top = 8.dp)
                ) {
                    itemsIndexed(widgets, key = { _, it -> it.id }) { index, widget ->
                        var offsetX by remember { mutableStateOf(0f) }
                        var isRefreshing by remember { mutableStateOf(false) }
                        val animOffset by animateFloatAsState(targetValue = offsetX, label = "swipe_${widget.id}")

                        // Velocity-based effects
                        val cardScale by animateFloatAsState(
                            targetValue = (1f - (Math.abs(velocity) / 2000f)).coerceIn(0.85f, 1f),
                            animationSpec = spring(stiffness = Spring.StiffnessLow),
                            label = "velocityScale"
                        )
                        val cardTilt by animateFloatAsState(
                            targetValue = (velocity / 200f).coerceIn(-5f, 5f),
                            animationSpec = spring(stiffness = Spring.StiffnessLow),
                            label = "velocityTilt"
                        )

                        // Entry Animation
                        var visible by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) {
                            delay(100L * index)
                            visible = true
                        }

                        AnimatedVisibility(
                            visible = visible,
                            enter = slideInVertically(
                                initialOffsetY = { it / 2 },
                                animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessLow)
                            ) + fadeIn()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .graphicsLayer {
                                        scaleX = cardScale
                                        scaleY = cardScale
                                        rotationX = cardTilt
                                        cameraDistance = 8 * density
                                    }
                                    .pointerInput(widget.id) {
                                        detectDragGestures(
                                            onDragEnd = {
                                                when {
                                                    offsetX > 100f -> {
                                                        offsetX = 0f
                                                        isRefreshing = true
                                                    }
                                                    offsetX < -100f -> {
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
                                            onTap = { 
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                onNavigateToDetail(widget.id) 
                                            },
                                            onLongPress = { 
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                showCardSheet = widget.id 
                                            }
                                        )
                                    }
                            ) {
                                // Swipe reveals
                                if (offsetX > 0) {
                                    Box(modifier = Modifier.fillMaxHeight().align(Alignment.CenterStart).padding(horizontal = 16.dp)) {
                                        Icon(Icons.Default.Refresh, null, tint = AuraMint, modifier = Modifier.size(24.dp))
                                    }
                                }
                                if (offsetX < 0) {
                                    Box(modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd).padding(horizontal = 16.dp)) {
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
                                            CircularProgressIndicator(color = AuraPrimary, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
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
                    CardSheetAction(Icons.AutoMirrored.Filled.OpenInNew, "View detail") { onNavigateToDetail(widgetId); showCardSheet = null }
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
            .hapticClickable(onClick = onClick)
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
    val haptic = LocalHapticFeedback.current
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
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onAdd()
            },
            colors = ButtonDefaults.buttonColors(containerColor = AuraPrimary),
            shape = ShapeButton
        ) {
            Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Create Widget", style = MaterialTheme.typography.labelLarge, color = AuraTextPrimary)
        }
    }
}
