package com.mystic.tarot.feature.shop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mystic.tarot.ui.theme.StarlightGold

@Composable
fun ShopScreen(
    viewModel: ShopViewModel,
    adManager: com.mystic.tarot.core.ads.AdManager
) {
    val coins by viewModel.coins.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as? android.app.Activity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Mistična Trgovina",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = StarlightGold
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Coin Balance Card
        Card(
            colors = CardDefaults.cardColors(containerColor = StarlightGold.copy(alpha = 0.2f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Zlatnici",
                    tint = StarlightGold,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "$coins Zlatnika",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // AdMob Rewarded Button
        Button(
            onClick = { 
                if (activity != null) {
                    adManager.showRewardedAd(activity) { rewardAmount ->
                         viewModel.onAdRewarded()
                         android.widget.Toast.makeText(context, "Zaradio si 50 zlatnika!", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = StarlightGold)
        ) {
             Icon(Icons.Default.PlayArrow, contentDescription = "Gledaj reklamu")
             Spacer(modifier = Modifier.width(8.dp))
             Text("Gledaj Reklamu (+50 🪙)", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Dostupni Artikli",
            fontSize = 20.sp,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.align(Alignment.Start)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Items List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                ShopItem(
                    name = "Dodatno Čitanje",
                    description = "Iskoristi zlatnike za još jedno instant čitanje sudbine.",
                    price = 50,
                    onBuy = { 
                        viewModel.buyItem(50, 
                            onSuccess = {
                                viewModel.addBonusReading()
                                android.widget.Toast.makeText(context, "Kupljeno! Novo čitanje dostupno.", android.widget.Toast.LENGTH_SHORT).show()
                            }, 
                            onError = {
                                android.widget.Toast.makeText(context, "Nemaš dovoljno zlatnika!", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        ) 
                    }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Dolazi Uskoro...",
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                    modifier = Modifier.fillMaxWidth().alpha(0.5f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("✨ Novi Špilovi Karata", color = Color.White)
                        Text("Otključaj unikatne ilustracije i nove energije.", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                    modifier = Modifier.fillMaxWidth().alpha(0.5f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("🚫 Bez Reklama", color = Color.White)
                        Text("Čisto iskustvo bez prekida.", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun ShopItem(
    name: String,
    description: String,
    price: Int,
    onBuy: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                Text(text = description, fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f))
            }
            
            Button(
                onClick = onBuy,
                colors = ButtonDefaults.buttonColors(containerColor = StarlightGold),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "$price", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
