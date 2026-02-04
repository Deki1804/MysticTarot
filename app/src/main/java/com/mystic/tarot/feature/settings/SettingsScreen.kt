package com.mystic.tarot.feature.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.mystic.tarot.R
import com.mystic.tarot.core.auth.AuthViewModel
import com.mystic.tarot.ui.theme.StarlightGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val user by authViewModel.user.collectAsState()
    val error by authViewModel.error.collectAsState()
    
    // Google Sign In Launcher for Linking
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            authViewModel.onLinkGoogleAccountResult(task)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Postavke", color = StarlightGold, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = StarlightGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A2E), // Match app theme
                    titleContentColor = StarlightGold
                )
            )
        },
        containerColor = Color(0xFF1A1A2E)
    ) { paddingValues ->
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            
            // Error Display
            if (error != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = error ?: "",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                LaunchedEffect(error) {
                    kotlinx.coroutines.delay(5000)
                    authViewModel.clearError()
                }
            }

            // SECTION: ACCOUNT
            SettingsSection(title = "Račun") {
                if (user?.isAnonymous == true) {
                    SettingsItem(
                        icon = Icons.Default.Link,
                        title = "Poveži Google Račun",
                        subtitle = "Sačuvaj napredak ako promijeniš uređaj",
                        onClick = {
                            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(context.getString(R.string.default_web_client_id))
                                .requestEmail()
                                .build()
                            val googleSignInClient = GoogleSignIn.getClient(context, gso)
                            launcher.launch(googleSignInClient.signInIntent)
                        }
                    )
                } else {
                     SettingsItem(
                        icon = Icons.Default.Email,
                        title = "Prijavljen kao",
                        subtitle = user?.email ?: "Google Korisnik",
                        showChevron = false
                    )
                }
                
                SettingsItem(
                    icon = Icons.Default.Logout,
                    title = "Odjava",
                    onClick = {
                        authViewModel.signOut()
                        navController.navigate("welcome") {
                            popUpTo(0) // Clear backstack
                        }
                    }
                )

                SettingsItem(
                    icon = Icons.Default.Delete,
                    title = "Obriši Račun",
                    subtitle = "Trajno brisanje svih podataka",
                    textColor = Color.Red,
                    iconColor = Color.Red,
                    onClick = {
                        // In a real app, show confirmation dialog first
                         authViewModel.deleteAccount()
                         navController.navigate("welcome") {
                            popUpTo(0)
                        }
                    }
                )
            }

            // SECTION: GENERAL
            SettingsSection(title = "Općenito") {
                SettingsItem(
                    icon = Icons.Default.Language,
                    title = "Jezik",
                    subtitle = "Hrvatski (Zadano)",
                    onClick = { /* Placeholder for language picker */ }
                )
                
                var notificationsEnabled by remember { mutableStateOf(true) }
                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "Dnevni Podsjetnik",
                    trailingContent = {
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = StarlightGold,
                                checkedTrackColor = StarlightGold.copy(alpha = 0.5f)
                            )
                        )
                    },
                    onClick = { notificationsEnabled = !notificationsEnabled }
                )
            }

            // SECTION: SUPPORT
            SettingsSection(title = "Podrška & Pravila") {
                SettingsItem(
                    icon = Icons.Default.Email,
                    title = "Kontaktiraj Nas",
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:larrydj@gmail.com")
                            putExtra(Intent.EXTRA_SUBJECT, "Mystic Tarot Support")
                        }
                        context.startActivity(Intent.createChooser(intent, "Send Email"))
                    }
                )
                
                SettingsItem(
                    icon = Icons.Default.Policy,
                    title = "Pravila Privatnosti",
                    onClick = {
                         val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Deki1804/MysticTarot/blob/main/privacy_policy.md")) // Replace with actual URL
                         context.startActivity(intent)
                    }
                )
            }
            
            // APP INFO
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Mystic Tarot AI v1.0.1",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = StarlightGold,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1658).copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    textColor: Color = Color.White,
    iconColor: Color = StarlightGold,
    showChevron: Boolean = true,
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
        
        if (trailingContent != null) {
            trailingContent()
        } else if (showChevron && onClick != null) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}
