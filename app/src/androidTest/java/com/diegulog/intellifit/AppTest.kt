package com.diegulog.intellifit

import android.app.Application
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin

class AppTest: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin { }
    }

    override fun onTerminate() {
        stopKoin()
        super.onTerminate()
    }
}