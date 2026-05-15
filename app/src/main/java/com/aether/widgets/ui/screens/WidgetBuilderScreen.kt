package com.aether.widgets.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aether.widgets.ui.components.*
import com.aether.widgets.ui.fake.*
import com.aether.widgets.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun WidgetBuilderScreen(
    onBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit = {},
    onAddToDashboard: () -> Unit = {}
) {
    var state by remember { mutableStateOf(BuilderState()) }

    AnimatedContent(
        targetState = state.phase,
        transitionSpec = {
            fadeIn(tween(300)) + slideInVertically { it / 8 } togetherWith
            fadeOut(tween(200))
        },
        label = "builder_phase"
    ) { phase ->
        when (phase) {
            BuilderPhase.PROMPT     -> PhaseA(state, onBack) { state = it }
            BuilderPhase.GENERATING -> PhaseB(state) { state = it }
            BuilderPhase.PREVIEW    -> PhaseC(state, { state = it }, { onNavigateToDetail(state.previewWidget?.id ?: "") })
            BuilderPhase.SIZE       -> PhaseD(state) { state = it }
            BuilderPhase.BEHAVIOUR  -> PhaseE(state) { state = it }
            BuilderPhase.CONFIRM    -> PhaseF(state, { state = it }, onAddToDashboard, onBack)
        }
    }
}

// ─── Phase A: Prompt Entry ────────────────────────────────────────────────────

@Composable
private fun PhaseA(
    state: BuilderState,
    onBack: () -> Unit,
    onStateChange: (BuilderState) -> Unit
) {
    var placeholderIdx by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            placeholderIdx = (placeholderIdx + 1) % FakeData.promptPlaceholders.size
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AuraBase)
            .padding(horizontal = 24.dp)
    ) {
        // TopBar
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = AuraTextSecondary)
            }
            Text("New Widget", style = MaterialTheme.typography.titleMedium, color = AuraTextPrimary)
        }

        Text("What should AURA surface?", style = MaterialTheme.typography.headlineMedium, color = AuraTextPrimary)
        Spacer(Modifier.height(24.dp))

        // Prompt field
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(ShapeSmall)
                .background(AuraSurfaceHigh)
                .border(1.dp, if (state.promptText.isEmpty()) AuraOutlineVariant else AuraPrimary.copy(alpha = 0.6f), ShapeSmall)
                .padding(16.dp)
        ) {
            if (state.promptText.isEmpty()) {
                AnimatedContent(targetState = placeholderIdx, label = "ph") { idx ->
                    Text(FakeData.promptPlaceholders[idx], style = MaterialTheme.typography.bodyMedium, color = AuraOutline)
                }
            }
            BasicTextField(
                value = state.promptText,
                onValueChange = { onStateChange(state.copy(promptText = it)) },
                modifier = Modifier.fillMaxSize(),
                textStyle = TextStyle(color = AuraTextPrimary, fontSize = 15.sp, fontFamily = FontFamily.Serif),
                cursorBrush = SolidColor(AuraPrimary)
            )
        }

        Spacer(Modifier.height(16.dp))
        Text("QUICK START", style = MaterialTheme.typography.labelSmall, color = AuraOutline)
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(FakeData.quickStartChips) { chip ->
                Box(
                    modifier = Modifier
                        .clip(ShapeChip)
                        .background(AuraSurfaceHigh)
                        .border(1.dp, AuraOutlineVariant, ShapeChip)
                        .clickable { onStateChange(state.copy(promptText = chip)) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(chip, style = MaterialTheme.typography.labelMedium, color = AuraTextSecondary, fontSize = 12.sp)
                }
            }
        }

        Spacer(Modifier.weight(1f))
        Button(
            onClick = { onStateChange(state.copy(phase = BuilderPhase.GENERATING)) },
            enabled = state.promptText.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AuraPrimary),
            shape = ShapeButton
        ) {
            Text("Build Widget", style = MaterialTheme.typography.labelLarge, color = AuraTextPrimary, letterSpacing = 1.sp)
        }
        Spacer(Modifier.height(24.dp))
    }
}

// ─── Phase B: Generating ─────────────────────────────────────────────────────

@Composable
private fun PhaseB(state: BuilderState, onStateChange: (BuilderState) -> Unit) {
    val statusTexts = listOf("Analysing your context...", "Building the layout...", "Generating content...", "Finalising widget...")
    var statusIdx by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        repeat(4) {
            delay(1000)
            statusIdx = (statusIdx + 1) % statusTexts.size
        }
        delay(500)
        // Build a fake preview widget and move to Preview phase
        val preview = FakeData.widgets.first()
        onStateChange(state.copy(phase = BuilderPhase.PREVIEW, previewWidget = preview))
    }

    val infiniteTransition = rememberInfiniteTransition(label = "gen")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "gen_alpha"
    )

    Column(
        modifier = Modifier.fillMaxSize().background(AuraBase).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Prompt chip
        Box(
            modifier = Modifier
                .clip(ShapeChip)
                .background(AuraSurfaceHigh)
                .border(1.dp, AuraOutlineVariant, ShapeChip)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(state.promptText.take(40), style = MaterialTheme.typography.labelMedium, color = AuraTextSecondary)
        }

        Spacer(Modifier.height(40.dp))

        // Pulsing rounded rect
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(ShapeCard)
                .background(AuraPrimary.copy(alpha = shimmerAlpha * 0.15f))
                .border(1.dp, AuraPrimary.copy(alpha = shimmerAlpha), ShapeCard)
        )

        Spacer(Modifier.height(32.dp))
        AnimatedContent(targetState = statusIdx, label = "status") { idx ->
            Text(statusTexts[idx], style = MaterialTheme.typography.bodyMedium, color = AuraTextSecondary)
        }
        Spacer(Modifier.height(24.dp))
        TextButton(onClick = { onStateChange(state.copy(phase = BuilderPhase.PROMPT)) }) {
            Text("Cancel", style = MaterialTheme.typography.labelMedium, color = AuraOutline)
        }
    }
}

// ─── Phase C: Preview & Iteration ────────────────────────────────────────────

@Composable
private fun PhaseC(
    state: BuilderState,
    onStateChange: (BuilderState) -> Unit,
    onViewDetail: () -> Unit
) {
    val widget = state.previewWidget ?: FakeData.widgets.first()
    val sizes = listOf("S", "M", "L")
    var selectedSize by remember { mutableStateOf(1) } // M
    var isDarkPreview by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize().background(AuraBase)) {
        // TopBar
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onStateChange(state.copy(phase = BuilderPhase.PROMPT)) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = AuraTextSecondary)
            }
            Text("Preview", style = MaterialTheme.typography.titleMedium, color = AuraTextPrimary, modifier = Modifier.weight(1f))
            IconButton(onClick = { isDarkPreview = !isDarkPreview }) {
                Icon(Icons.Default.Refresh, null, tint = AuraOutline)
            }
        }

        // Preview zone (55%)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.55f)
                .background(if (isDarkPreview) AuraBase else Color(0xFFF5F5F0)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Size pills
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(AuraSurfaceHigh)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    sizes.forEachIndexed { i, s ->
                        if (i == selectedSize) {
                            Button(
                                onClick = { selectedSize = i },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AuraSurfaceHighest),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                                elevation = ButtonDefaults.buttonElevation(0.dp)
                            ) { Text(s, style = MaterialTheme.typography.labelMedium, color = AuraTextPrimary) }
                        } else {
                            TextButton(
                                onClick = { selectedSize = i },
                                shape = RoundedCornerShape(16.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                            ) { Text(s, style = MaterialTheme.typography.labelMedium, color = AuraOutline) }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                WidgetCard(
                    widget = widget,
                    modifier = Modifier.fillMaxWidth(0.85f)
                )
            }
        }

        // Controls (45%)
        Column(
            modifier = Modifier
                .weight(0.45f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(AuraSurfaceHigh)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Tone selector
            BuilderSegmentedRow("CONTENT TONE", listOf("Minimal", "Balanced", "Rich"), 1)
            // Text size
            BuilderSegmentedRow("TEXT SIZE", listOf("Small", "Default", "Large"), 1)
            // Accent
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("ACCENT", style = MaterialTheme.typography.labelSmall, color = AuraOutline, modifier = Modifier.width(80.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(AuraPrimary, AuraGold, AuraMint, AuraRose).forEach { c ->
                        Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(c).border(2.dp, if (c == AuraPrimary) AuraTextPrimary else Color.Transparent, CircleShape))
                    }
                }
            }

            Divider(color = AuraOutlineVariant.copy(alpha = 0.3f))

            OutlinedButton(
                onClick = { onStateChange(state.copy(phase = BuilderPhase.GENERATING)) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                border = BorderStroke(1.dp, AuraOutlineVariant),
                shape = ShapeButton
            ) {
                Icon(Icons.Default.Refresh, null, tint = AuraTextSecondary, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Regenerate", style = MaterialTheme.typography.labelMedium, color = AuraTextSecondary)
            }

            Button(
                onClick = { onStateChange(state.copy(phase = BuilderPhase.SIZE)) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AuraPrimary),
                shape = ShapeButton
            ) {
                Text("Continue to Size & Layout", style = MaterialTheme.typography.labelLarge, color = AuraTextPrimary)
            }
        }
    }
}

@Composable
private fun BuilderSegmentedRow(label: String, options: List<String>, selectedIdx: Int) {
    var sel by remember { mutableStateOf(selectedIdx) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = AuraOutline)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(ShapeSmall)
                .background(AuraSurfaceHighest)
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            options.forEachIndexed { i, opt ->
                if (i == sel) {
                    Button(
                        onClick = { sel = i },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = AuraSurfaceHigh),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) { Text(opt, style = MaterialTheme.typography.labelMedium, color = AuraTextPrimary) }
                } else {
                    TextButton(
                        onClick = { sel = i },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) { Text(opt, style = MaterialTheme.typography.labelMedium, color = AuraOutline) }
                }
            }
        }
    }
}

// ─── Phase D: Size & Layout ───────────────────────────────────────────────────

@Composable
private fun PhaseD(state: BuilderState, onStateChange: (BuilderState) -> Unit) {
    var selectedSize by remember { mutableStateOf(WidgetSize.MEDIUM) }
    val sizes = listOf(
        Triple(WidgetSize.SMALL, "Small", "2×1 — compact at-a-glance"),
        Triple(WidgetSize.MEDIUM, "Medium", "2×2 — standard widget"),
        Triple(WidgetSize.LARGE, "Large", "4×2 — rich data display"),
        Triple(WidgetSize.COMPACT, "Compact", "1×1 — minimal icon")
    )

    Column(modifier = Modifier.fillMaxSize().background(AuraBase).padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp)) {
            IconButton(onClick = { onStateChange(state.copy(phase = BuilderPhase.PREVIEW)) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = AuraTextSecondary)
            }
            Text("Size & Layout", style = MaterialTheme.typography.headlineMedium, color = AuraTextPrimary)
        }

        sizes.forEach { (size, name, desc) ->
            val isSelected = selectedSize == size
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(ShapeTile)
                    .background(if (isSelected) AuraPrimary.copy(alpha = 0.1f) else AuraSurfaceHigh)
                    .border(1.dp, if (isSelected) AuraPrimary else AuraOutlineVariant.copy(alpha = 0.5f), ShapeTile)
                    .clickable { selectedSize = size }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(name, style = MaterialTheme.typography.bodyMedium, color = if (isSelected) AuraPrimary else AuraTextPrimary)
                    Text(desc, style = MaterialTheme.typography.bodySmall, color = AuraTextSecondary)
                }
                if (isSelected) Icon(Icons.Default.CheckCircle, null, tint = AuraPrimary, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.weight(1f))
        Button(
            onClick = { onStateChange(state.copy(phase = BuilderPhase.BEHAVIOUR, selectedSize = selectedSize)) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AuraPrimary),
            shape = ShapeButton
        ) {
            Text("Continue to Behaviour", style = MaterialTheme.typography.labelLarge, color = AuraTextPrimary)
        }
    }
}

// ─── Phase E: Behaviour ───────────────────────────────────────────────────────

@Composable
private fun PhaseE(state: BuilderState, onStateChange: (BuilderState) -> Unit) {
    var refreshIdx by remember { mutableStateOf(2) } // Hourly
    var offlineIdx by remember { mutableStateOf(0) } // Last Known
    var allowOrchestration by remember { mutableStateOf(true) }
    var confidenceThreshold by remember { mutableStateOf(0.7f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AuraBase)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp)) {
            IconButton(onClick = { onStateChange(state.copy(phase = BuilderPhase.SIZE)) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = AuraTextSecondary)
            }
            Text("Behaviour", style = MaterialTheme.typography.headlineMedium, color = AuraTextPrimary)
        }

        SectionHeader("REFRESH SCHEDULE", Modifier.padding(top = 0.dp))
        BuilderSegmentedRow("", listOf("Live", "15m", "Hourly", "Daily"), refreshIdx)

        SectionHeader("OFFLINE MODE")
        BuilderSegmentedRow("", listOf("Last Known", "Skeleton", "Hide"), offlineIdx)

        SectionHeader("ORCHESTRATION")
        Row(
            modifier = Modifier.fillMaxWidth().clip(ShapeTile).background(AuraSurfaceHigh).ghostBorder(12).padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Allow context suppression", style = MaterialTheme.typography.bodyMedium, color = AuraTextPrimary)
                Text("AURA may hide this widget during Focus sessions", style = MaterialTheme.typography.bodySmall, color = AuraTextSecondary)
            }
            Switch(
                checked = allowOrchestration,
                onCheckedChange = { allowOrchestration = it },
                colors = SwitchDefaults.colors(checkedTrackColor = AuraPrimary, uncheckedTrackColor = AuraSurfaceHighest, checkedThumbColor = AuraTextPrimary, uncheckedThumbColor = AuraOutline)
            )
        }
        Spacer(Modifier.height(16.dp))
        Column(modifier = Modifier.fillMaxWidth().clip(ShapeTile).background(AuraSurfaceHigh).ghostBorder(12).padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Confidence threshold", style = MaterialTheme.typography.bodyMedium, color = AuraTextPrimary)
                Text("${(confidenceThreshold * 100).toInt()}%", style = MaterialTheme.typography.labelMedium, color = AuraPrimary)
            }
            Slider(
                value = confidenceThreshold,
                onValueChange = { confidenceThreshold = it },
                valueRange = 0.5f..1f,
                colors = SliderDefaults.colors(thumbColor = AuraPrimary, activeTrackColor = AuraPrimary, inactiveTrackColor = AuraSurfaceHighest)
            )
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { onStateChange(state.copy(phase = BuilderPhase.CONFIRM, confidenceThreshold = confidenceThreshold)) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AuraPrimary),
            shape = ShapeButton
        ) {
            Text("Continue to Confirm", style = MaterialTheme.typography.labelLarge, color = AuraTextPrimary)
        }
        Spacer(Modifier.height(24.dp))
    }
}

// ─── Phase F: Confirmation ────────────────────────────────────────────────────

@Composable
private fun PhaseF(
    state: BuilderState,
    onStateChange: (BuilderState) -> Unit,
    onAddToDashboard: () -> Unit,
    onDiscard: () -> Unit
) {
    val widget = state.previewWidget ?: FakeData.widgets.first()
    var widgetName by remember { mutableStateOf(widget.displayName) }
    var showDiscardSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AuraBase)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp)) {
            IconButton(onClick = { onStateChange(state.copy(phase = BuilderPhase.BEHAVIOUR)) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = AuraTextSecondary)
            }
            Text("Confirm", style = MaterialTheme.typography.headlineMedium, color = AuraTextPrimary)
        }

        // Hero preview
        WidgetCard(widget = widget, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(24.dp))

        // Editable name
        Text("WIDGET NAME", style = MaterialTheme.typography.labelSmall, color = AuraOutline, modifier = Modifier.padding(bottom = 8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(ShapeSmall)
                .background(AuraSurfaceHigh)
                .border(1.dp, AuraOutlineVariant, ShapeSmall)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = widgetName,
                onValueChange = { widgetName = it },
                textStyle = TextStyle(color = AuraTextPrimary, fontSize = 15.sp, fontFamily = FontFamily.Serif),
                cursorBrush = SolidColor(AuraPrimary),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(16.dp))

        // Spec card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(ShapeTile)
                .background(AuraSurfaceHigh)
                .ghostBorder(12)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SpecRow("Type", widget.type.name.lowercase().replaceFirstChar { it.uppercase() })
            SpecRow("Size", state.selectedSize.name.lowercase().replaceFirstChar { it.uppercase() })
            SpecRow("Refresh", "Hourly")
            SpecRow("Offline mode", "Last Known")
            SpecRow("Orchestration", "Enabled")
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = onAddToDashboard,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AuraPrimary),
            shape = ShapeButton
        ) {
            Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Add to AURA", style = MaterialTheme.typography.labelLarge, color = AuraTextPrimary)
        }
        Spacer(Modifier.height(12.dp))
        TextButton(
            onClick = { showDiscardSheet = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Discard widget", style = MaterialTheme.typography.labelMedium, color = AuraRose)
        }
        Spacer(Modifier.height(24.dp))
    }

    if (showDiscardSheet) {
        ConfirmSheet(
            title = "Discard widget?",
            body = "All builder progress will be lost.",
            confirmLabel = "Discard",
            onConfirm = onDiscard,
            onDismiss = { showDiscardSheet = false }
        )
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = AuraTextSecondary)
        Text(value, style = MaterialTheme.typography.bodySmall, color = AuraTextPrimary)
    }
}
