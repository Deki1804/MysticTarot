package com.mystic.tarot.core.util

import com.mystic.tarot.BuildConfig
import android.util.Log

/**
 * Wrapper za logove – u release buildu ne šalje Log.d/w/v, da se smanji šum.
 * Log.e se također šalje samo u DEBUG (za production dodaj Crashlytics itd.).
 */
object LogUtil {
    fun d(tag: String, message: String) {
        if (BuildConfig.DEBUG) Log.d(tag, message)
    }

    fun w(tag: String, message: String) {
        if (BuildConfig.DEBUG) Log.w(tag, message)
    }

    fun e(tag: String, message: String) {
        if (BuildConfig.DEBUG) Log.e(tag, message)
    }

    fun e(tag: String, message: String, throwable: Throwable?) {
        if (BuildConfig.DEBUG) Log.e(tag, message, throwable)
    }
}
