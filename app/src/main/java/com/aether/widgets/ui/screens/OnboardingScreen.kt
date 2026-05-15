package com.aether.widgets.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.ui.draw.alpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aether.widgets.data.KeyManager
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import com.aether.widgets.ui.components.AuraOnboardingTopAppBar
import com.aether.widgets.ui.components.AuraSwitch
import com.aether.widgets.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    keyManager: KeyManager,
    onComplete: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 5 })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AuraBase)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AuraOnboardingTopAppBar(
                currentStep = pagerState.currentPage + 1,
                totalSteps = 5,
                onClose = { /* Could reset or minimize */ }
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                userScrollEnabled = false,
                beyondBoundsPageCount = 1
            ) { page ->
                when (page) {
                    0 -> OnboardingWelcomeStep(onBegin = { scope.launch { pagerState.animateScrollToPage(1) } })
                    1 -> OnboardingApiKeyStep(
                        keyManager = keyManager,
                        onContinue = { scope.launch { pagerState.animateScrollToPage(2) } }
                    )
                    2 -> OnboardingPermissionsStep(onContinue = { scope.launch { pagerState.animateScrollToPage(3) } })
                    3 -> OnboardingMoodStep(
                        keyManager = keyManager,
                        onContinue = { scope.launch { pagerState.animateScrollToPage(4) } }
                    )
                    4 -> OnboardingFirstWidgetStep(onBuild = onComplete, onSkip = onComplete)
                }
            }
        }
    }
}

@Composable
private fun OnboardingStepContainer(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
    footer: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 120.dp) // Leave space for footer
                .verticalScroll(rememberScrollState()),
            content = content
        )
        
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            content = { footer() }
        )
    }
}

// ─── Step 1: Welcome (Screen 11) ─────────────────────────────────────────────

@Composable
private fun OnboardingWelcomeStep(onBegin: () -> Unit) {
    OnboardingStepContainer(
        content = {
            Spacer(modifier = Modifier.height(32.dp))

            // Bento-style preview (Conceptual)
            Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                Row(modifier = Modifier.fillMaxWidth().height(128.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    BentoCard(Modifier.weight(1f), AuraPrimary, Icons.Default.DateRange, index = 0)
                    BentoCard(Modifier.weight(1f), AuraMint, Icons.Default.Eco, index = 1)
                }
                BentoCard(
                    Modifier.fillMaxWidth().height(160.dp).align(Alignment.BottomCenter),
                    AuraRose,
                    Icons.Default.Waves,
                    isWide = true,
                    index = 2
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Your home screen, finally intelligent.",
                style = MaterialTheme.typography.displayMedium,
                color = AuraTextPrimary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "AURA learns what matters to you and surfaces it. Nothing else.",
                style = MaterialTheme.typography.bodyLarge,
                color = AuraTextSecondary,
                modifier = Modifier.fillMaxWidth()
            )
        },
        footer = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AuraOnboardingButton(text = "Begin", onClick = onBegin)
                
                Text(
                    text = "TAKES ABOUT 2 MINUTES",
                    style = MaterialTheme.typography.labelSmall,
                    color = AuraTextSecondary,
                    modifier = Modifier.padding(top = 16.dp).alpha(0.6f)
                )
            }
        }
    )
}

@Composable
private fun BentoCard(
    modifier: Modifier, 
    color: Color, 
    icon: ImageVector, 
    isWide: Boolean = false,
    index: Int = 0
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100L * index)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = expandVertically(animationSpec = spring(stiffness = Spring.StiffnessLow)) + fadeIn(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(ShapeCard)
                .background(AuraSurface.copy(alpha = 0.5f))
                .border(2.dp, color.copy(alpha = 0.4f), ShapeCard),
            contentAlignment = if (isWide) Alignment.CenterStart else Alignment.Center
        ) {
            // Gradient overlay
            Box(modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(color.copy(alpha = 0.1f), Color.Transparent))
            ))
            
            if (isWide) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Box(Modifier.size(48.dp, 8.dp).clip(CircleShape).background(color.copy(alpha = 0.4f)))
                    Spacer(Modifier.height(8.dp))
                    Box(Modifier.size(128.dp, 16.dp).clip(CircleShape).background(color.copy(alpha = 0.2f)))
                    Spacer(Modifier.height(8.dp))
                    Box(Modifier.size(96.dp, 16.dp).clip(CircleShape).background(color.copy(alpha = 0.2f)))
                }
            } else {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            }
        }
    }
}

// ─── Step 2: API Key (Screen 12) ──────────────────────────────────────────────

@Composable
private fun OnboardingApiKeyStep(keyManager: KeyManager, onContinue: () -> Unit) {
    var apiKey by remember { mutableStateOf(keyManager.getGeminiKey() ?: "") }
    var visible by remember { mutableStateOf(false) }

    OnboardingStepContainer(
        content = {
            Spacer(modifier = Modifier.height(32.dp))

            Text(text = "Connect your AI.", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Your API key is stored in your phone's hardware encryption chip (Android Keystore), ensuring maximum security for your requests.",
                style = MaterialTheme.typography.bodyLarge,
                color = AuraTextSecondary
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Input Box
            Column {
                Text(
                    text = "CLAUDE API KEY",
                    style = MaterialTheme.typography.labelSmall,
                    color = AuraPrimary,
                    modifier = Modifier.padding(start = 12.dp, bottom = 4.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AuraSurface)
                        .border(1.dp, AuraOutlineVariant, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BasicTextField(
                            value = apiKey,
                            onValueChange = { apiKey = it },
                            modifier = Modifier.weight(1f),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = AuraTextPrimary,
                                fontFamily = FontFamily.Monospace
                            ),
                            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            cursorBrush = SolidColor(AuraPrimary)
                        )
                        IconButton(onClick = { visible = !visible }) {
                            Icon(
                                imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = AuraTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        IconButton(onClick = { /* Paste from clipboard logic */ }) {
                            Icon(
                                imageVector = Icons.Default.ContentPaste,
                                contentDescription = null,
                                tint = AuraTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Security Assurance
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AuraSurfaceHigh)
                    .border(1.dp, AuraSurfaceHighest, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(Icons.Default.Lock, null, tint = AuraPrimary, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Keys never leave your device. All API requests are routed directly to Anthropic from your network.",
                    style = MaterialTheme.typography.bodySmall,
                    color = AuraTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = { /* Test connection */ },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(AuraOutlineVariant))
            ) {
                Text("Test Connection", color = AuraTextPrimary)
            }
        },
        footer = {
            AuraOnboardingButton(
                text = "Continue",
                onClick = {
                    keyManager.saveGeminiKey(apiKey)
                    onContinue()
                },
                enabled = apiKey.isNotEmpty()
            )
        }
    )
}

// ─── Step 3: Permissions (Screen 14) ──────────────────────────────────────────

@Composable
private fun OnboardingPermissionsStep(onContinue: () -> Unit) {
    OnboardingStepContainer(
        content = {
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "What AURA can see.", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(32.dp))

            val permissions = listOf(
                PermissionItem("Location", "Recommended", "Used for weather and commute estimates.", Icons.Default.LocationOn, true),
                PermissionItem("Calendar", "Recommended", "Helps AURA understand your schedule.", Icons.Default.CalendarMonth, true),
                PermissionItem("Notifications", "Optional", "For silent background updates only.", Icons.Default.Notifications, false),
                PermissionItem("Battery Optimization", "Optional", "Allows background refresh without interruption.", Icons.Default.BatterySaver, false)
            )

            permissions.forEach { item ->
                PermissionRow(item)
                Spacer(Modifier.height(32.dp))
            }
        },
        footer = {
            AuraOnboardingButton(text = "Continue", onClick = onContinue)
        }
    )
}

private data class PermissionItem(val name: String, val tag: String, val desc: String, val icon: ImageVector, val initial: Boolean)

@Composable
private fun PermissionRow(item: PermissionItem) {
    var checked by remember { mutableStateOf(item.initial) }
    
    // Feedback scale for the row when toggled
    val rowScale by animateFloatAsState(
        targetValue = if (checked) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "rowScale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = rowScale
                scaleY = rowScale
            }
            .clip(RoundedCornerShape(12.dp))
            .background(if (checked) AuraPrimary.copy(alpha = 0.05f) else Color.Transparent)
            .padding(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            item.icon, 
            null, 
            tint = if (checked) AuraPrimary else AuraTextSecondary, 
            modifier = Modifier.size(24.dp).padding(top = 2.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.name, 
                    style = MaterialTheme.typography.titleMedium, 
                    fontWeight = FontWeight.Bold,
                    color = if (checked) AuraTextPrimary else AuraTextSecondary
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (item.tag == "Recommended") AuraPrimary.copy(0.2f) else AuraTextSecondary.copy(0.2f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.tag.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = if (item.tag == "Recommended") AuraPrimary else AuraTextSecondary
                    )
                }
            }
            Text(text = item.desc, style = MaterialTheme.typography.bodySmall, color = AuraTextSecondary)
        }
        AuraSwitch(
            checked = checked,
            onCheckedChange = { checked = it }
        )
    }
}

// ─── Step 4: Mood (Screen 15) ────────────────────────────────────────────────

@Composable
private fun OnboardingMoodStep(keyManager: KeyManager, onContinue: () -> Unit) {
    var selectedMood by remember { mutableStateOf(keyManager.getDesignMood()) }

    OnboardingStepContainer(
        content = {
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "Set the mood.", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "How should AURA speak to you? Choose the level of detail and tone for your daily insights.",
                style = MaterialTheme.typography.bodyLarge,
                color = AuraTextSecondary
            )

            Spacer(modifier = Modifier.height(48.dp))

            MoodCard("Minimal", "Just the facts.", Icons.AutoMirrored.Filled.ShortText, selectedMood == "Minimal") { 
                selectedMood = "Minimal"
                keyManager.saveDesignMood("Minimal")
            }
            Spacer(Modifier.height(16.dp))
            MoodCard("Balanced", "Thoughtful context.", Icons.Default.Tune, selectedMood == "Balanced") { 
                selectedMood = "Balanced"
                keyManager.saveDesignMood("Balanced")
            }
            Spacer(Modifier.height(16.dp))
            MoodCard("Rich", "Deep exploration.", Icons.AutoMirrored.Filled.MenuBook, selectedMood == "Rich") { 
                selectedMood = "Rich"
                keyManager.saveDesignMood("Rich")
            }
        },
        footer = {
            AuraOnboardingButton(text = "Continue", onClick = onContinue)
        }
    )
}

@Composable
private fun MoodCard(title: String, desc: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    val rotation by animateFloatAsState(
        targetValue = if (isSelected) 0f else -2f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
        label = "rotation"
    )

    val iconRotation by animateFloatAsState(
        targetValue = if (isSelected) 360f else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessVeryLow),
        label = "iconRotation"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                rotationZ = if (isPressed) rotation else 0f
            }
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) AuraPrimary.copy(0.08f) else AuraSurface)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) AuraPrimary else AuraOutlineVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title, 
                    style = MaterialTheme.typography.headlineSmall, 
                    color = if (isSelected) AuraPrimary else AuraTextPrimary,
                    modifier = Modifier.animateContentSize()
                )
                Text(
                    text = desc, 
                    style = MaterialTheme.typography.labelSmall, 
                    color = if (isSelected) AuraTextPrimary else AuraTextSecondary,
                    modifier = Modifier.alpha(if (isSelected) 1f else 0.7f)
                )
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) AuraPrimary.copy(0.2f) else AuraBase)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon, 
                    contentDescription = null, 
                    tint = if (isSelected) AuraPrimary else AuraTextSecondary,
                    modifier = Modifier.graphicsLayer {
                        rotationZ = iconRotation
                    }
                )
            }
        }
    }
}

// ─── Step 5: First Widget (Screen 13) ────────────────────────────────────────

@Composable
private fun OnboardingFirstWidgetStep(onBuild: () -> Unit, onSkip: () -> Unit) {
    var prompt by remember { mutableStateOf("") }

    OnboardingStepContainer(
        content = {
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "Build your first widget.", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Describe what you want. AURA will build it.",
                style = MaterialTheme.typography.bodyLarge,
                color = AuraTextSecondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp) // Slightly taller for better typing experience
                    .clip(RoundedCornerShape(16.dp))
                    .background(AuraSurface)
                    .border(1.dp, AuraOutlineVariant, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                if (prompt.isEmpty()) {
                    Text(
                        text = "Try: Daily weather for my commute", 
                        color = AuraTextTertiary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                BasicTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    modifier = Modifier.fillMaxSize(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = AuraTextPrimary),
                    cursorBrush = SolidColor(AuraPrimary)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "SUGGESTIONS",
                style = MaterialTheme.typography.labelSmall,
                color = AuraPrimary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()), 
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf("Daily Insight", "Weather", "News", "Calendar", "Habits").forEach {
                    SuggestionChip(
                        onClick = { prompt = it },
                        label = { Text(it) },
                        shape = CircleShape,
                        border = AssistChipDefaults.assistChipBorder(
                            enabled = true, 
                            borderColor = AuraOutlineVariant
                        ),
                        colors = AssistChipDefaults.assistChipColors(
                            labelColor = AuraTextPrimary,
                            containerColor = AuraSurfaceHigh
                        )
                    )
                }
            }
        },
        footer = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AuraOnboardingButton(
                    text = "Build It", 
                    onClick = onBuild, 
                    enabled = prompt.isNotEmpty()
                )
                
                TextButton(
                    onClick = onSkip,
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                ) {
                    Text(
                        text = "Skip — I'll build widgets later.", 
                        style = MaterialTheme.typography.bodyMedium,
                        color = AuraTextSecondary
                    )
                }
            }
        }
    )
}

@Composable
private fun AuraOnboardingButton(text: String, onClick: () -> Unit, enabled: Boolean = true) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow),
        label = "buttonScale"
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AuraPrimary,
            disabledContainerColor = AuraPrimary.copy(alpha = 0.3f)
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        )
    }
}
