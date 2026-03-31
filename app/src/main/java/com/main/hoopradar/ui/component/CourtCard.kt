package com.main.hoopradar.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.main.hoopradar.data.model.Court

@Composable
fun CourtCard(court: Court) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(court.name)
            Text(court.address)
            Text("${court.distanceMiles ?: "?"} miles away")
        }
    }
}