package com.mystic.tarot.core.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class CoinRepository(private val context: Context) {

    companion object {
        val COIN_BALANCE_KEY = intPreferencesKey("coin_balance")
        val LAST_READING_DAY_KEY = intPreferencesKey("last_reading_day")
        val BONUS_READINGS_KEY = intPreferencesKey("bonus_readings")
        const val INITIAL_COINS = 100
    }

    val coins: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[COIN_BALANCE_KEY] ?: INITIAL_COINS
        }

    val canDoReading: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            val lastReadingDay = preferences[LAST_READING_DAY_KEY] ?: 0
            val bonusReadings = preferences[BONUS_READINGS_KEY] ?: 0
            val today = getCurrentDayInt()
            val canRead = (lastReadingDay != today) || (bonusReadings > 0)
            android.util.Log.d("CoinRepository", "Check Limit: Last=$lastReadingDay, Today=$today, Bonus=$bonusReadings, CanRead=$canRead")
            canRead
        }

    suspend fun addCoins(amount: Int) {
        context.dataStore.edit { preferences ->
            val current = preferences[COIN_BALANCE_KEY] ?: INITIAL_COINS
            preferences[COIN_BALANCE_KEY] = current + amount
        }
    }

    suspend fun spendCoins(amount: Int): Boolean {
        var success = false
        context.dataStore.edit { preferences ->
            val current = preferences[COIN_BALANCE_KEY] ?: INITIAL_COINS
            if (current >= amount) {
                preferences[COIN_BALANCE_KEY] = current - amount
                success = true
            } else {
                success = false
            }
        }
        return success
    }

    suspend fun addBonusReading() {
        context.dataStore.edit { preferences ->
            val current = preferences[BONUS_READINGS_KEY] ?: 0
            preferences[BONUS_READINGS_KEY] = current + 1
        }
    }

    suspend fun markReadingDone() {
        val today = getCurrentDayInt()
        context.dataStore.edit { preferences ->
            val lastReadingDay = preferences[LAST_READING_DAY_KEY] ?: 0
            
            if (lastReadingDay != today) {
                // Consumed Daily Free Reading
                preferences[LAST_READING_DAY_KEY] = today
                android.util.Log.d("CoinRepository", "Marked Reading Done (Daily Free used).")
            } else {
                // Consumed Bonus Reading
                val bonus = preferences[BONUS_READINGS_KEY] ?: 0
                if (bonus > 0) {
                    preferences[BONUS_READINGS_KEY] = bonus - 1
                    android.util.Log.d("CoinRepository", "Marked Reading Done (Bonus used). Remaining: ${bonus - 1}")
                } else {
                    android.util.Log.w("CoinRepository", "Marked Reading Done but NO readings available! Logic error?")
                }
            }
        }
    }

    private fun getCurrentDayInt(): Int {
        val calendar = java.util.Calendar.getInstance()
        val year = calendar.get(java.util.Calendar.YEAR)
        val month = calendar.get(java.util.Calendar.MONTH) + 1 // 0-indexed
        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        return year * 10000 + month * 100 + day // YYYYMMDD
    }
}
