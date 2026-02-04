package com.mystic.tarot.core.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class AnalyticsHelper(context: Context) {

    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    fun logEvent(eventName: String, params: Map<String, String> = emptyMap()) {
        val bundle = Bundle()
        params.forEach { (key, value) ->
            bundle.putString(key, value)
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    fun logScreenView(screenName: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    companion object {
        const val EVENT_APP_OPEN = "app_open_mystic"
        const val EVENT_DAILY_CARD_DRAWN = "daily_card_drawn"
        const val EVENT_READING_COMPLETED = "reading_completed"
        const val EVENT_JOURNAL_OPENED = "journal_opened"
        const val EVENT_AD_WATCHED = "ad_watched_reward"
        const val EVENT_READING_STARTED = "reading_started"
        const val EVENT_READING_BLOCKED = "reading_blocked"
    }
}
