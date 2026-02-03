package com.mystic.tarot.feature.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mystic.tarot.core.data.model.Reading
import com.mystic.tarot.ui.theme.StarlightGold
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun JournalScreen(
    viewModel: JournalViewModel
) {
    val readings by viewModel.readings.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Moj Dnevnik",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = StarlightGold
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (readings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = "Tvoja sudbina je još neispisana.\nOdaberi kartu da započneš putovanje.",
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(readings) { reading ->
                    ReadingItem(reading)
                }
            }
        }
    }
}

@Composable
fun ReadingItem(reading: Reading) {
    val formatter = SimpleDateFormat("dd. MMM yyyy., HH:mm", Locale.getDefault())
    val dateString = reading.date.toDate().let { formatter.format(it) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dateString,
                    fontSize = 12.sp,
                    color = StarlightGold.copy(alpha = 0.8f)
                )
                Text(
                    text = if (reading.type == "daily") "Dnevna Karta" else "Čitanje",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (reading.question.isNotEmpty()) {
                Text(
                    text = "\"${reading.question}\"",
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = Color.White,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Text(
                text = reading.interpretation.take(100) + "...",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        }
    }
}
