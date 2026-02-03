package com.mystic.tarot.feature.reading

import android.util.Log
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mystic.tarot.core.ai.TarotAiService
import com.mystic.tarot.core.model.TarotDeck
import com.mystic.tarot.ui.components.FlipCard
import com.mystic.tarot.ui.theme.StarlightGold
import kotlinx.coroutines.launch
import com.mystic.tarot.core.data.JournalRepository
import com.mystic.tarot.core.analytics.AnalyticsHelper
import com.mystic.tarot.core.data.model.Reading

import com.mystic.tarot.core.data.CoinRepository

import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.MonetizationOn
import com.mystic.tarot.core.ads.AdManager

@Composable
fun ReadingScreen(
    journalRepository: JournalRepository,
    analyticsHelper: AnalyticsHelper,
    userId: String,
    coinRepository: CoinRepository,
    adManager: AdManager
) {
    val scope = rememberCoroutineScope()
    val aiService = remember { TarotAiService() }
    
    var readingText by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isCardFlipped by remember { mutableStateOf(false) }
    var currentCardName by remember { mutableStateOf("") }
    var currentCardUrl by remember { mutableStateOf<String?>(null) }
    var currentQuestion by remember { mutableStateOf<String?>(null) }
    
    val canDoReading by coinRepository.canDoReading.collectAsState(initial = true)

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Daily Guidance",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = StarlightGold
            )

            // Card Interaction Area
            // Card Interaction Area
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!canDoReading && !isCardFlipped && readingText == null && !isLoading) {
                    // LIMIT REACHED STATE
                     Card(
                        colors = CardDefaults.cardColors(containerColor = StarlightGold.copy(alpha = 0.1f)),
                        modifier = Modifier.padding(16.dp).fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                             Text(
                                "Mistični Limit Dosegnut",
                                fontSize = 20.sp, 
                                fontWeight = FontWeight.Bold,
                                color = StarlightGold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Zvijezde su ti danas već otkrile put. Vrati se sutra za novo čitanje.",
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            val activity = androidx.compose.ui.platform.LocalContext.current as? android.app.Activity
                            
                             Button(
                                onClick = { 
                                    if (activity != null) {
                                        adManager.showRewardedAd(activity) {
                                            scope.launch {
                                                coinRepository.addBonusReading()
                                                // Automatic refresh relies on flow collection
                                            }
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = StarlightGold)
                            ) {
                                androidx.compose.material.icons.Icons.Default.PlayArrow
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.PlayArrow,
                                    contentDescription = "Watch Ad",
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Gledaj Reklamu (+1 Čitanje)", color = Color.Black)
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            val coins by coinRepository.coins.collectAsState(initial = 0)
                            
                            Button(
                                onClick = { 
                                    scope.launch {
                                        if (coinRepository.spendCoins(50)) {
                                            coinRepository.addBonusReading()
                                        }
                                    }
                                },
                                enabled = coins >= 50,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = StarlightGold)
                            ) {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.MonetizationOn,
                                    contentDescription = "Pay Coins",
                                    tint = if (coins >= 50) StarlightGold else Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Plati Zlatnicima (50 💰)")
                            }
                        }
                    }
                } else if (!isCardFlipped && !isLoading && readingText == null) {
                    // Initial State (Card Back / Draw Button)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Question Input
                        var questionText by remember { mutableStateOf("") }

                        OutlinedTextField(
                            value = questionText,
                            onValueChange = { questionText = it },
                            label = { Text("Postavi pitanje (opcionalno)", color = StarlightGold) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = StarlightGold,
                                unfocusedBorderColor = StarlightGold.copy(alpha = 0.5f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Box(
                            modifier = Modifier
                                .height(500.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            // Card Back styling
                            // Shimmer Animation
                            val shimmerColors = listOf(
                                StarlightGold.copy(alpha = 0.1f),
                                StarlightGold.copy(alpha = 0.5f),
                                StarlightGold.copy(alpha = 0.1f),
                            )
                            val transition = rememberInfiniteTransition(label = "shimmer")
                            val translateAnim = transition.animateFloat(
                                initialValue = 0f,
                                targetValue = 1000f,
                                animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                                    animation = tween(durationMillis = 1500, easing = androidx.compose.animation.core.LinearEasing),
                                    repeatMode = androidx.compose.animation.core.RepeatMode.Restart
                                ),
                                label = "shimmerTranslate"
                            )
                            val brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                colors = shimmerColors,
                                start = androidx.compose.ui.geometry.Offset.Zero,
                                end = androidx.compose.ui.geometry.Offset(x = translateAnim.value, y = translateAnim.value)
                            )

                            // Card Back styling
                            Card(
                                modifier = Modifier
                                    .width(300.dp)
                                    .fillMaxHeight(),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent), // Transparent to show background
                                border = androidx.compose.foundation.BorderStroke(2.dp, StarlightGold)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                                colors = listOf(
                                                    Color(0xFF2D1658), // Deep Cosmic Purple
                                                    Color(0xFF1A1A2E)  // Dark Blue/Black
                                                )
                                            )
                                        )
                                        .background(brush), // Shimmer overlay
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "?",
                                        fontSize = 80.sp,
                                        color = StarlightGold.copy(alpha = 0.5f)
                                    )
                                }
                            }
                            
                             val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
                            Button(
                                onClick = {
                                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                    if (!isLoading) {
                                        isLoading = true
                                        scope.launch {
                                            // 1. Draw a random card
                                            val randomCard = TarotDeck.majorArcana.random()
                                            currentCardName = randomCard.name
                                            currentCardUrl = randomCard.imageUrl
                                            val q = questionText.ifBlank { null }
                                            currentQuestion = q
                                            
                                            // 2. Get AI Reading
                                            val aiResponse = aiService.getReading(randomCard, q)
                                            
                                            // 3. Reveal
                                            readingText = aiResponse
                                            isLoading = false
                                            isCardFlipped = true
                                            
                                            // 4. Save & Track
                                            // Enforce limit FIRST (Local)
                                            coinRepository.markReadingDone()
                                            
                                            val reading = Reading(
                                                userId = userId,
                                                question = q ?: "",
                                                cardIds = emptyList(), // TODO: Map real IDs if we had them
                                                interpretation = aiResponse,
                                                type = "daily" // Default for now
                                            )
                                            
                                            // Then save to Cloud (Best effort)
                                            journalRepository.saveReading(reading)
                                            analyticsHelper.logEvent(AnalyticsHelper.EVENT_READING_COMPLETED)
                                            analyticsHelper.logEvent(AnalyticsHelper.EVENT_DAILY_CARD_DRAWN)
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .height(56.dp)
                                    .align(Alignment.BottomCenter)
                                    .offset(y = (-32).dp),
                                colors = ButtonDefaults.buttonColors(containerColor = StarlightGold)
                            ) {
                                Text("Izvuci Kartu", color = Color.Black)
                            }
                        }
                    }
                } else if (isLoading) {
                     Box(modifier = Modifier.height(500.dp), contentAlignment = Alignment.Center) {
                         Column(horizontalAlignment = Alignment.CenterHorizontally) {
                             CircularProgressIndicator(color = StarlightGold)
                             Text(
                                 "\nKonzultiram zvijezde...",
                                 color = Color.White.copy(alpha = 0.7f),
                                 textAlign = TextAlign.Center,
                                 modifier = Modifier.padding(top = 48.dp)
                             )
                         }
                     }
                } else {
                    // Result View
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = currentCardName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        
                        if (currentQuestion != null) {
                            Text(
                                text = "\"$currentQuestion\"",
                                fontSize = 16.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = StarlightGold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Card Image
                        if (currentCardUrl != null) {
                            coil.compose.AsyncImage(
                                model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                                    .data(currentCardUrl)
                                    .crossfade(true)
                                    .setHeader("User-Agent", "MysticTarot/1.0 (Android)") // Fix for Wikimedia 403
                                    .listener(
                                        onStart = { Log.d("Coil", "Start loading: $currentCardUrl") },
                                        onSuccess = { _, _ -> Log.d("Coil", "Success loading: $currentCardUrl") },
                                        onError = { request, result -> Log.e("Coil", "Error loading: $currentCardUrl", result.throwable) }
                                    )
                                    .build(),
                                contentDescription = currentCardName,
                                modifier = Modifier
                                    .height(450.dp)
                                    .width(300.dp)
                                    .padding(bottom = 16.dp),
                                contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                                placeholder = androidx.compose.ui.res.painterResource(com.mystic.tarot.R.drawable.ic_mystic_icon), // Fallback to icon for now if no placeholder
                                error = androidx.compose.ui.res.painterResource(com.mystic.tarot.R.drawable.ic_mystic_icon) // Show something on error
                            )
                        }

                        // Reading Text
                         Card(
                            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = readingText ?: "",
                                color = Color.White,
                                modifier = Modifier.padding(16.dp),
                                lineHeight = 24.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(
                            onClick = {
                                isCardFlipped = false
                                readingText = null
                                currentCardName = ""
                                currentCardUrl = null
                                currentQuestion = null
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = StarlightGold)
                        ) {
                            Text("Novo Čitanje")
                        }
                    }
                }
            }
        }
    }
}
