package com.omniimpact.aicolorthemes.utility

import android.util.Log
import kotlin.reflect.KClass

object ClassLog {
    private const val TAG = "🪾LOG🌳"
    private const val CRASH_TAG = "🪾LOG🔥"

    fun d(clazz: KClass<*>, message: String) {
        Log.d(TAG, "${clazz.simpleName}: $message")
    }

    fun e(clazz: KClass<*>, message: String, throwable: Throwable? = null) {
        Log.e(TAG, "${clazz.simpleName}: $message", throwable)
    }

    fun registerDefaultCrashHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e(CRASH_TAG, "Uncaught exception", throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
}
