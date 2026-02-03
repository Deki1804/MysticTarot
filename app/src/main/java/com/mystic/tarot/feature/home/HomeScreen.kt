package com.mystic.tarot.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mystic.tarot.core.data.CoinRepository
import com.mystic.tarot.ui.theme.StarlightGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    coinRepository: CoinRepository
) {
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
            Text(
                "Dobrodošli u Mystic Tarot",
                color = StarlightGold,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            // Ideally we'd have a big "Draw Card" CTA here or summary, 
            // but for now user navigates via bottom bar.
        }
    }
}
