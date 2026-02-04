package com.mystic.tarot.feature.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mystic.tarot.core.data.CoinRepository
import com.mystic.tarot.ui.theme.StarlightGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    coinRepository: CoinRepository,
    analyticsHelper: com.mystic.tarot.core.analytics.AnalyticsHelper
) {
    androidx.compose.runtime.LaunchedEffect(Unit) {
        analyticsHelper.logEvent(com.mystic.tarot.core.analytics.AnalyticsHelper.EVENT_APP_OPEN)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pocket Tarot AI", color = StarlightGold, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = StarlightGold
                ),
                actions = {
                    // Coins Display
                    val coins by coinRepository.coins.collectAsState(initial = 0)
                    Text(
                        text = "$coins 💰",
                        color = StarlightGold,
                        modifier = Modifier.padding(end = 16.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Settings Icon
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = StarlightGold
                        )
                    }
                }
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.layout.Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    "Dobrodošli, tražitelju.",
                    color = StarlightGold,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    "Udahni duboko. Fokusiraj se na trenutak. Kad budeš spreman, započni svoj dnevni ritual na donjem izborniku.",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Visual ritual cue
                Text(
                    "✨",
                    fontSize = 48.sp
                )
            }
        }
    }
}
