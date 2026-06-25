package com.omniimpact.aicolorthemes.utility

import android.util.Log
import kotlin.reflect.KClass

/**
 * A utility class for standardized logging across the application.
 */
object ClassLog {
    private const val TAG = "🪾LOG🌳"
    private const val CRASH_TAG = "🪾LOG🔥"

    /**
     * Logs a debug message with the simple class name as part of the tag.
     *
     * @param clazz The class to log for.
     * @param message The debug message to log.
     */
    fun d(clazz: KClass<*>, message: String) {
        Log.d(TAG, "${clazz.simpleName}: $message")
    }

    /**
     * Logs an error message with the simple class name as part of the tag, optionally with a throwable.
     *
     * @param clazz The class to log for.
     * @param message The error message to log.
     * @param throwable The optional throwable associated with the error.
     */
    fun e(clazz: KClass<*>, message: String, throwable: Throwable? = null) {
        Log.e(TAG, "${clazz.simpleName}: $message", throwable)
    }

    /**
     * Registers a global uncaught exception handler that logs crash details to logcat.
     */
    fun registerDefaultCrashHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e(CRASH_TAG, "Uncaught exception", throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
}
