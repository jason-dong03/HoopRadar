package com.main.hoopradar.ui.screen.court

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.main.hoopradar.ui.common.AppScaffold
import com.main.hoopradar.ui.component.GlassCard
import com.main.hoopradar.ui.component.PrimaryButton
import com.main.hoopradar.ui.theme.*

@Composable
fun CourtDetailsScreen(onBack: () -> Unit) {
    AppScaffold(title = "Court Details", showBackButton = true, onBack = onBack) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(DeepNavy, DarkSurface)))
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    CourtDetailRow(Icons.Filled.LocationOn, "Court", "Emmet Street Courts")
                    HorizontalDivider(color = GlassBorder)
                    CourtDetailRow(Icons.Filled.NearMe, "Address", "UVA Charlottesville, VA")
                    HorizontalDivider(color = GlassBorder)
                    CourtDetailRow(Icons.Filled.NearMe, "Distance", "0.8 miles away")
                    HorizontalDivider(color = GlassBorder)
                    CourtDetailRow(Icons.Filled.SportsBasketball, "Active Runs", "2 upcoming")
                }
            }

            Spacer(Modifier.weight(1f))

            PrimaryButton(text = "Create Run at this Court", onClick = {})
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun CourtDetailRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = HoopOrange, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Text(value, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
        }
    }
}
