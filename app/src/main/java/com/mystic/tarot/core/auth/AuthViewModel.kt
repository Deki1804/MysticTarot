package com.mystic.tarot.core.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Simple factory since we are not using Hilt yet
class AuthViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _user = MutableStateFlow<FirebaseUser?>(null)
    val user: StateFlow<FirebaseUser?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(true) // Start loading initially
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        // Observe auth state
        viewModelScope.launch {
            repository.authState.collect { firebaseUser ->
                _user.value = firebaseUser
                _isLoading.value = false // Done check on startup
            }
        }
    }

    fun signInAsGuest() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.signInAnonymously()
            result.onSuccess {
                _isLoading.value = false
            }.onFailure { e ->
                _error.value = "Failed to sign in as guest: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun onGoogleSignInResult(task: com.google.android.gms.tasks.Task<com.google.android.gms.auth.api.signin.GoogleSignInAccount>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
                val idToken = account.idToken
                if (idToken != null) {
                    val result = repository.signInWithGoogle(idToken)
                    result.onFailure { e ->
                       _error.value = "Google Auth Failed: ${e.message}"
                    }
                } else {
                    _error.value = "Google Auth Failed: No ID Token"
                }
            } catch (e: com.google.android.gms.common.api.ApiException) {
                _error.value = "Google Sign In Failed: ${e.statusCode}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
