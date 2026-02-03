package com.mystic.tarot.core.data.model

import com.google.firebase.Timestamp

data class Reading(
    val id: String = "",
    val userId: String = "",
    val date: Timestamp = Timestamp.now(),
    val question: String = "",
    val cardIds: List<Int> = emptyList(),
    val interpretation: String = "",
    val type: String = "daily" // "daily", "spread", "yes_no"
)
