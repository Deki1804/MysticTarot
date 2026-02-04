package com.mystic.tarot.core.auth

import com.mystic.tarot.core.util.LogUtil
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
    suspend fun linkWithGoogle(idToken: String): Result<FirebaseUser>
    suspend fun deleteAccount(): Result<Unit>
    fun signOut()
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
                LogUtil.d("AuthRepository", "SignInAnonymously: success, uid=${user.uid}")
                Result.success(user)
            } else {
                Result.failure(Exception("Sign in successful but user is null"))
            }
        } catch (e: Exception) {
            LogUtil.e("AuthRepository", "SignInAnonymously: failure", e)
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> {
         return try {
            val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user
             if (user != null) {
                LogUtil.d("AuthRepository", "SignInWithGoogle: success, uid=${user.uid}")
                Result.success(user)
            } else {
                Result.failure(Exception("Google Sign in successful but user is null"))
            }
        } catch (e: Exception) {
            LogUtil.e("AuthRepository", "SignInWithGoogle: failure", e)
            Result.failure(e)
        }
    }

    override suspend fun linkWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("No user logged in"))
            val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
            val result = user.linkWithCredential(credential).await()
            val linkedUser = result.user
            if (linkedUser != null) {
                LogUtil.d("AuthRepository", "LinkWithGoogle: success, uid=${linkedUser.uid}")
                Result.success(linkedUser)
            } else {
                Result.failure(Exception("Link successful but user is null"))
            }
        } catch (e: Exception) {
            LogUtil.e("AuthRepository", "LinkWithGoogle: failure", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("No user logged in"))
            user.delete().await()
            LogUtil.d("AuthRepository", "DeleteAccount: success")
            Result.success(Unit)
        } catch (e: Exception) {
            LogUtil.e("AuthRepository", "DeleteAccount: failure", e)
            Result.failure(e)
        }
    }

    override fun signOut() {
        auth.signOut()
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}
