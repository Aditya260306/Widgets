package com.aether.widgets.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.aether.widgets.data.KeyManager
import com.aether.widgets.ui.theme.AetherTheme
import com.aether.widgets.ui.screens.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var keyManager: KeyManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AetherTheme(keyManager = keyManager) {
                AuraApp(keyManager)
            }
        }
    }
}

// ─── Screen state ─────────────────────────────────────────────────────────────

private sealed class Screen {
    object Onboarding : Screen()
    object Dashboard  : Screen()
    object Builder    : Screen()
    data class Detail(val widgetId: String) : Screen()
    object Context    : Screen()
    object Settings   : Screen()
    object Fidget     : Screen()
}

// ─── Root composable ──────────────────────────────────────────────────────────

@Composable
fun AuraApp(keyManager: KeyManager) {
    var screen by remember {
        mutableStateOf<Screen>(
            if (keyManager.isOnboardingCompleted()) Screen.Dashboard else Screen.Onboarding
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                val enter = when (targetState) {
                    Screen.Fidget -> slideInVertically { it } + fadeIn(tween(300))
                    Screen.Dashboard -> slideInHorizontally { -it / 8 } + fadeIn(tween(300))
                    else -> slideInHorizontally { it / 8 } + fadeIn(tween(300))
                }
                val exit = when (initialState) {
                    Screen.Fidget -> slideOutVertically { it } + fadeOut(tween(250))
                    else -> fadeOut(tween(200))
                }
                enter togetherWith exit
            },
            label = "screen_nav"
        ) { s ->
            when (s) {
                Screen.Onboarding -> OnboardingScreen(
                    keyManager = keyManager,
                    onComplete = { 
                        keyManager.setOnboardingCompleted(true)
                        screen = Screen.Dashboard 
                    }
                )
                Screen.Dashboard -> DashboardScreen(
                    onNavigateToBuilder  = { screen = Screen.Builder },
                    onNavigateToContext  = { screen = Screen.Context },
                    onNavigateToSettings = { screen = Screen.Settings },
                    onNavigateToFidgetSpace = { screen = Screen.Fidget },
                    onNavigateToDetail   = { id -> screen = Screen.Detail(id) }
                )
                Screen.Builder -> WidgetBuilderScreen(
                    onBack = { screen = Screen.Dashboard },
                    onNavigateToDetail = { id -> screen = Screen.Detail(id) },
                    onAddToDashboard   = { screen = Screen.Dashboard }
                )
                is Screen.Detail -> WidgetDetailScreen(
                    onBack = { screen = Screen.Dashboard },
                    widgetId = s.widgetId,
                    onNavigateToBuilder = { screen = Screen.Builder }
                )
                Screen.Context -> ContextMonitorScreen(
                    onBack = { screen = Screen.Dashboard }
                )
                Screen.Settings -> SettingsScreen(
                    onBack = { screen = Screen.Dashboard },
                    onReset = {
                        keyManager.resetAll()
                        screen = Screen.Onboarding
                    }
                )
                Screen.Fidget -> FidgetSpaceScreen(
                    onDismiss = { screen = Screen.Dashboard }
                )
            }
        }
    }
}
