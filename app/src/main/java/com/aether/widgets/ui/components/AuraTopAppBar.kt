package com.aether.widgets.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aether.widgets.ui.theme.AuraTextSecondary

@Composable
fun AuraOnboardingTopAppBar(
    currentStep: Int,
    totalSteps: Int,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Spacer to center if needed, but design has it on the right
        }

        Text(
            text = "$currentStep / $totalSteps",
            style = MaterialTheme.typography.labelMedium.copy(
                color = AuraTextSecondary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                letterSpacing = 1.sp
            ),
            modifier = Modifier.padding(end = 8.dp)
        )
    }
}
