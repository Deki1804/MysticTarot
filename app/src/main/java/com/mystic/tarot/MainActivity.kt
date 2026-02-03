package com.mystic.tarot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.mystic.tarot.core.auth.AuthRepositoryImpl
import com.mystic.tarot.core.auth.AuthViewModel
import com.mystic.tarot.core.auth.AuthViewModelFactory
import com.mystic.tarot.navigation.AppNavigation
import com.mystic.tarot.feature.welcome.WelcomeScreen
import com.mystic.tarot.ui.components.SplashScreen
import com.mystic.tarot.ui.theme.MysticTarotTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        com.mystic.tarot.core.notifications.NotificationHelper.createNotificationChannel(this)
        
        // Permission Launcher (Android 13+)
        val requestPermissionLauncher = registerForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                com.mystic.tarot.core.notifications.NotificationHelper.scheduleDailyReminder(this)
            }
        }
        
        // Request on start
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
             if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != 
                 android.content.pm.PackageManager.PERMISSION_GRANTED) {
                 requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
             } else {
                 com.mystic.tarot.core.notifications.NotificationHelper.scheduleDailyReminder(this)
             }
        } else {
            com.mystic.tarot.core.notifications.NotificationHelper.scheduleDailyReminder(this)
        }
        
        // Manual DI for now
        val authRepository = AuthRepositoryImpl()
        val authViewModelFactory = AuthViewModelFactory(authRepository)
        
        val coinRepository = com.mystic.tarot.core.data.CoinRepository(applicationContext)
        val journalRepository = com.mystic.tarot.core.data.JournalRepositoryImpl()
        val analyticsHelper = com.mystic.tarot.core.analytics.AnalyticsHelper(applicationContext)

        val billingManager = com.mystic.tarot.core.billing.BillingManager(this, coinRepository)
        billingManager.initialize() // Initialize RevenueCat
        
        val shopViewModelFactory = com.mystic.tarot.feature.shop.ShopViewModelFactory(coinRepository, billingManager)
        
        // Initialize AdManager
        val adManager = com.mystic.tarot.core.ads.AdManager(this)
        val settingsRepository = com.mystic.tarot.core.data.SettingsRepository(this)

        setContent {
            MysticTarotTheme {
                val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
                val user by authViewModel.user.collectAsState()
                val isLoading by authViewModel.isLoading.collectAsState()
                val error by authViewModel.error.collectAsState()

                if (isLoading) {
                    SplashScreen(message = "Summoning Spirits...")
                } else if (user != null) {
                    AppNavigation(
                        shopViewModelFactory = shopViewModelFactory,
                        adManager = adManager,
                        journalRepository = journalRepository,
                        analyticsHelper = analyticsHelper,
                        userId = user!!.uid,
                        coinRepository = coinRepository,
                        settingsRepository = settingsRepository
                    )
                } else {
                    WelcomeScreen(
                        onSignInAsGuest = { authViewModel.signInAsGuest() },
                        onGoogleSignInResult = { task -> authViewModel.onGoogleSignInResult(task) }
                    )
                    
                    if (error != null) {
                        android.widget.Toast.makeText(
                            androidx.compose.ui.platform.LocalContext.current,
                            error,
                            android.widget.Toast.LENGTH_LONG
                        ).show()
                        authViewModel.clearError()
                    }
                }
            }
        }
    }
}
