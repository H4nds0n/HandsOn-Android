package com.handson.handson

import android.app.Application
import android.content.Context

class HandsOn : Application() {
    companion object {
        lateinit var appContext: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}