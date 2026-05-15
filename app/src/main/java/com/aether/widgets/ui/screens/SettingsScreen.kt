package com.aether.widgets.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aether.widgets.ui.components.*
import com.aether.widgets.ui.fake.FakeData
import com.aether.widgets.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit, onReset: () -> Unit) {
    val ctx = FakeData.context
    var notificationsEnabled by remember { mutableStateOf(true) }
    var focusAutoDetect by remember { mutableStateOf(true) }
    var analyticsEnabled by remember { mutableStateOf(false) }
    var selectedMode by remember { mutableStateOf(0) } // 0=Quiet 1=Standard 2=Verbose

    Scaffold(
        containerColor = AuraBase,
        topBar = {
            TopAppBar(
                title = { Text("Engine Room", style = MaterialTheme.typography.headlineMedium, color = AuraTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = AuraTextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AuraBase.copy(alpha = 0.95f))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // ─── Profile card ─────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(ShapeTile)
                    .background(AuraSurface)
                    .ghostBorder(12)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(52.dp).clip(CircleShape).background(AuraPrimary.copy(alpha = 0.15f)).border(1.dp, AuraPrimary.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("A", style = MaterialTheme.typography.headlineMedium, color = AuraPrimary)
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("Aditya", style = MaterialTheme.typography.titleLarge, color = AuraTextPrimary)
                    Text("${ctx.apiCallsUsed}/${ctx.apiCallsLimit} API calls · Free plan", style = MaterialTheme.typography.bodySmall, color = AuraTextSecondary)
                }
            }
            Spacer(Modifier.height(24.dp))

            // ─── API Key ─────────────────────────────────────────────────────
            SectionHeader("AI ENGINE")
            SettingsGroup {
                SettingsRow(
                    icon = Icons.Default.Key,
                    iconTint = AuraPrimary,
                    title = "Gemini API Key",
                    subtitle = "•••• •••• •••• Av2X",
                    trailingIcon = Icons.Default.ChevronRight
                ) {}
                Divider(color = AuraOutlineVariant.copy(alpha = 0.3f))
                SettingsRow(
                    icon = Icons.Default.TrendingUp,
                    iconTint = AuraGold,
                    title = "API Usage",
                    subtitle = "${ctx.apiCallsUsed} of ${ctx.apiCallsLimit} calls used today",
                    trailingIcon = Icons.Default.ChevronRight
                ) {}
            }

            // ─── Intelligence ─────────────────────────────────────────────────
            SectionHeader("INTELLIGENCE")
            SettingsGroup {
                SettingsRow(
                    icon = Icons.Default.Memory,
                    iconTint = AuraMint,
                    title = "Update mode",
                    subtitle = listOf("Quiet", "Standard", "Verbose")[selectedMode],
                    trailingIcon = Icons.Default.ChevronRight
                ) { selectedMode = (selectedMode + 1) % 3 }
                Divider(color = AuraOutlineVariant.copy(alpha = 0.3f))
                SettingsToggleRow(
                    icon = Icons.Default.DoNotDisturb,
                    iconTint = AuraPrimary,
                    title = "Auto-detect Focus",
                    subtitle = "Suppress widgets during focus sessions",
                    checked = focusAutoDetect,
                    onCheckedChange = { focusAutoDetect = it }
                )
            }

            // ─── Notifications ────────────────────────────────────────────────
            SectionHeader("NOTIFICATIONS")
            SettingsGroup {
                SettingsToggleRow(
                    icon = Icons.Default.Notifications,
                    iconTint = AuraGold,
                    title = "Widget alerts",
                    subtitle = "Push notifications for important updates",
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )
                Divider(color = AuraOutlineVariant.copy(alpha = 0.3f))
                SettingsRow(
                    icon = Icons.Default.Schedule,
                    iconTint = AuraOutline,
                    title = "Quiet hours",
                    subtitle = "22:00 – 07:00",
                    trailingIcon = Icons.Default.ChevronRight
                ) {}
            }

            // ─── Privacy ──────────────────────────────────────────────────────
            SectionHeader("PRIVACY")
            SettingsGroup {
                SettingsToggleRow(
                    icon = Icons.Default.Analytics,
                    iconTint = AuraOutline,
                    title = "Usage analytics",
                    subtitle = "Help improve AURA (anonymous)",
                    checked = analyticsEnabled,
                    onCheckedChange = { analyticsEnabled = it }
                )
                Divider(color = AuraOutlineVariant.copy(alpha = 0.3f))
                SettingsRow(
                    icon = Icons.Default.DeleteOutline,
                    iconTint = AuraRose,
                    title = "Clear all data",
                    subtitle = "Remove widgets, history, and cache",
                    trailingIcon = Icons.Default.ChevronRight
                ) {}
                Divider(color = AuraOutlineVariant.copy(alpha = 0.3f))
                SettingsRow(
                    icon = Icons.Default.Refresh,
                    iconTint = AuraRose,
                    title = "Reset AURA",
                    subtitle = "Factory reset and restart onboarding",
                    trailingIcon = Icons.Default.ChevronRight
                ) {
                    onReset()
                }
            }

            // ─── About ────────────────────────────────────────────────────────
            SectionHeader("ABOUT")
            SettingsGroup {
                SettingsRow(
                    icon = Icons.Default.Info,
                    iconTint = AuraOutline,
                    title = "AURA",
                    subtitle = "Version 1.0.0 — Phase 1 UI Shell",
                    trailingIcon = null
                ) {}
                Divider(color = AuraOutlineVariant.copy(alpha = 0.3f))
                SettingsRow(
                    icon = Icons.Default.Code,
                    iconTint = AuraOutline,
                    title = "Open source",
                    subtitle = "github.com/aether/widgets",
                    trailingIcon = Icons.Default.OpenInNew
                ) {}
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

// ─── Settings group wrapper ───────────────────────────────────────────────────
@Composable
private fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(ShapeTile).background(AuraSurface).ghostBorder(12),
        content = content
    )
    Spacer(Modifier.height(8.dp))
}

// ─── Settings row ─────────────────────────────────────────────────────────────
@Composable
private fun SettingsRow(
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    title: String,
    subtitle: String,
    trailingIcon: ImageVector?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(36.dp).clip(ShapeSmall).background(iconTint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, color = AuraTextPrimary)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = AuraTextSecondary, fontSize = 11.sp)
        }
        if (trailingIcon != null) {
            Icon(trailingIcon, null, tint = AuraOutline, modifier = Modifier.size(18.dp))
        }
    }
}

// ─── Settings toggle row ──────────────────────────────────────────────────────
@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(36.dp).clip(ShapeSmall).background(iconTint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, color = AuraTextPrimary)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = AuraTextSecondary, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = AuraPrimary,
                checkedThumbColor = AuraTextPrimary,
                uncheckedTrackColor = AuraSurfaceHighest,
                uncheckedThumbColor = AuraOutline
            )
        )
    }
}
