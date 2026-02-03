package com.mystic.tarot.feature.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mystic.tarot.core.data.JournalRepository
import com.mystic.tarot.core.data.model.Reading
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class JournalViewModel(
    private val repository: JournalRepository,
    userId: String
) : ViewModel() {

    val readings: StateFlow<List<Reading>> = repository.getReadings(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

}

class JournalViewModelFactory(
    private val repository: JournalRepository,
    private val userId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JournalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JournalViewModel(repository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
