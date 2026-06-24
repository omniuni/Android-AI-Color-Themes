package com.omniimpact.aicolorthemes

import android.app.Application
import com.omniimpact.aicolorthemes.utility.ClassLog

class AIColorThemesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ClassLog.registerDefaultCrashHandler()
    }
}
