package com.mystic.tarot.feature.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mystic.tarot.core.data.SettingsRepository
import com.mystic.tarot.core.notifications.NotificationHelper
import com.mystic.tarot.ui.theme.MysticPurple
import com.mystic.tarot.ui.theme.StarlightGold
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    settingsRepository: SettingsRepository,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val notificationsEnabled by settingsRepository.notificationsEnabled.collectAsState(initial = true)
    
    // Background gradient
    val brush = Brush.verticalGradient(
        colors = listOf(Color.Black, MysticPurple, Color.Black)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Text("⬅", color = StarlightGold, fontSize = 24.sp)
            }
            Text(
                "Postavke & Info",
                color = StarlightGold,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Notifications Section
        Text(
            "Obavijesti",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Dnevni podsjetnik",
                color = Color.White.copy(alpha = 0.8f)
            )
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = { enabled ->
                    scope.launch {
                        settingsRepository.setNotificationsEnabled(enabled)
                        if (enabled) {
                            NotificationHelper.scheduleDailyReminder(context)
                        } else {
                            // In a real app we'd cancel the work, but WorkManager needs ID
                            // For V1, scheduling implies 'enabled'. To strictly cancel we'd need to add cancel logic to Helper.
                            // Adding logic now:
                            androidx.work.WorkManager.getInstance(context).cancelUniqueWork("daily_reminder_work")
                        }
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = StarlightGold,
                    checkedTrackColor = MysticPurple,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.DarkGray
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        HorizontalDivider(color = StarlightGold.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(32.dp))

        // Legal Section
        Text(
            "Pravne Napomene",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            "Ova aplikacija je isključivo zabavnog karaktera. Tarot čitanja generira umjetna inteligencija (AI) i ne smiju se smatrati stučnim, medicinskim, pravnim ili financijskim savjetima.",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            "Ne donosite važne životne odluke na temelju ovih čitanja. Za stručnu pomoć obratite se kvalificiranom stručnjaku.",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Privacy Policy Button
        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.freeprivacypolicy.com/live/placeholder")) 
                // Placeholder URL - User needs to provide real one
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    // Fallback
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f))
        ) {
            Text("Politika Privatnosti (Privacy Policy)", color = StarlightGold)
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            "Verzija 1.0.0",
            color = Color.White.copy(alpha = 0.3f),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 12.sp
        )
    }
}
