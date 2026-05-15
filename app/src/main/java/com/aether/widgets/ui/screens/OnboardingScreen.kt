package com.aether.widgets.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aether.widgets.data.KeyManager
import com.aether.widgets.ui.theme.AuraBase
import com.aether.widgets.ui.theme.AuraGhostBorder
import com.aether.widgets.ui.theme.AuraPrimary
import com.aether.widgets.ui.theme.AuraSurface
import com.aether.widgets.ui.theme.AuraTextPrimary
import com.aether.widgets.ui.theme.AuraTextSecondary
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
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = false
        ) { page ->
            when (page) {
                0 -> StitchCenteredStep(
                    title = "Welcome to Aura",
                    subtitle = "Quiet luxury for your home screen.",
                    buttonText = "BEGIN",
                    onNext = { scope.launch { pagerState.animateScrollToPage(1) } }
                )

                1 -> StitchApiKeyStep(
                    keyManager = keyManager,
                    onNext = { scope.launch { pagerState.animateScrollToPage(2) } }
                )

                2 -> StitchCenteredStep(
                    title = "Permissions",
                    subtitle = "Aura needs Location and Calendar\naccess to build the Context\nSnapshot.",
                    buttonText = "GRANT ACCESS",
                    onNext = { scope.launch { pagerState.animateScrollToPage(3) } }
                )

                3 -> StitchCenteredStep(
                    title = "Design Mood",
                    subtitle = "Calibrating the tonal layering...",
                    buttonText = "CONTINUE",
                    onNext = { scope.launch { pagerState.animateScrollToPage(4) } }
                )

                4 -> StitchCenteredStep(
                    title = "First Widget",
                    subtitle = "Let's build your first insight.",
                    buttonText = "ENTER MISSION CONTROL",
                    onNext = onComplete
                )
            }
        }
    }
}

@Composable
private fun StitchCenteredStep(
    title: String,
    subtitle: String,
    buttonText: String,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(240.dp))

        Text(
            text = title,
            color = AuraTextPrimary,
            style = MaterialTheme.typography.displayLarge.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 62.sp,
                lineHeight = 72.sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = subtitle,
            color = AuraTextSecondary,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = FontFamily.Serif,
                fontSize = 20.sp,
                lineHeight = 34.sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(160.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AuraPrimary)
        ) {
            Text(
                text = buttonText,
                color = AuraTextPrimary,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            )
        }
    }
}

@Composable
private fun StitchApiKeyStep(
    keyManager: KeyManager,
    onNext: () -> Unit
) {
    var apiKey by remember { mutableStateOf(keyManager.getGeminiKey() ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(250.dp))

        Text(
            text = "API Key",
            color = AuraTextPrimary,
            style = MaterialTheme.typography.displayLarge.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 62.sp,
                lineHeight = 72.sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(44.dp))

        Text(
            text = "GEMINI API KEY",
            modifier = Modifier.fillMaxWidth(),
            color = AuraTextSecondary,
            style = MaterialTheme.typography.titleSmall.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(AuraSurface)
                .border(1.dp, AuraGhostBorder, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            BasicTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                modifier = Modifier.fillMaxSize(),
                textStyle = TextStyle(
                    color = AuraTextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 18.sp
                ),
                cursorBrush = SolidColor(AuraPrimary)
            )
        }

        Spacer(modifier = Modifier.height(150.dp))

        Button(
            onClick = {
                keyManager.saveGeminiKey(apiKey)
                onNext()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AuraPrimary)
        ) {
            Text(
                text = "SAVE & CONTINUE",
                color = AuraTextPrimary,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            )
        }
    }
}
