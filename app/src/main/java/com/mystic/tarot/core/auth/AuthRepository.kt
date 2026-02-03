package com.mystic.tarot.core.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

interface AuthRepository {
    val authState: Flow<FirebaseUser?>
    suspend fun signInAnonymously(): Result<FirebaseUser>
    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser>
    fun getCurrentUser(): FirebaseUser?
}

// Ensure you have a way to provide this (Singleton usually)
class AuthRepositoryImpl : AuthRepository {
    
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override val authState: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signInAnonymously(): Result<FirebaseUser> {
        return try {
            val result = auth.signInAnonymously().await()
            val user = result.user
            if (user != null) {
                Log.d("AuthRepository", "SignInAnonymously: success, uid=${user.uid}")
                Result.success(user)
            } else {
                Result.failure(Exception("Sign in successful but user is null"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "SignInAnonymously: failure", e)
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> {
         return try {
            val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user
             if (user != null) {
                Log.d("AuthRepository", "SignInWithGoogle: success, uid=${user.uid}")
                Result.success(user)
            } else {
                Result.failure(Exception("Google Sign in successful but user is null"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "SignInWithGoogle: failure", e)
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}
