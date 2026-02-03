package com.mystic.tarot.core.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mystic.tarot.core.data.model.Reading
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

interface JournalRepository {
    suspend fun saveReading(reading: Reading): Result<Unit>
    fun getReadings(userId: String): Flow<List<Reading>>
}

class JournalRepositoryImpl : JournalRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val readingsCollection = firestore.collection("readings")

    override suspend fun saveReading(reading: Reading): Result<Unit> {
        return try {
            val docRef = if (reading.id.isEmpty()) {
                readingsCollection.document()
            } else {
                readingsCollection.document(reading.id)
            }
            
            val finalReading = reading.copy(id = docRef.id)
            docRef.set(finalReading).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getReadings(userId: String): Flow<List<Reading>> = callbackFlow {
        val subscription = readingsCollection
            .whereEqualTo("userId", userId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val readings = snapshot.toObjects(Reading::class.java)
                    trySend(readings)
                }
            }

        awaitClose { subscription.remove() }
    }
}
