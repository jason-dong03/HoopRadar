package com.main.hoopradar.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.main.hoopradar.data.model.Run
import com.main.hoopradar.ui.theme.*

@Composable
fun RunCard(run: Run, modifier: Modifier = Modifier) {
    val alpha = if (run.isFull) 0.45f else 1f

    GlassCard(modifier = modifier.fillMaxWidth().alpha(alpha)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = run.courtName.ifBlank { "Court Run" },
                    style = MaterialTheme.typography.titleMedium,
                    color = if (run.isFull) TextMuted else TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                if (run.isFull) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(TextMuted.copy(alpha = 0.3f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("Full", style = MaterialTheme.typography.labelMedium, color = TextMuted)
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.AccessTime, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(run.dateTime, style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Group, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${run.currentPlayers}/${run.maxPlayers}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (run.isFull) ErrorRed.copy(alpha = 0.7f) else TextSecondary
                    )
                }
            }
            if (run.skillLevel.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (run.isFull) TextMuted.copy(alpha = 0.1f) else HoopOrange.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = run.skillLevel,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (run.isFull) TextMuted else HoopOrangeLight
                    )
                }
            }
        }
    }
}
