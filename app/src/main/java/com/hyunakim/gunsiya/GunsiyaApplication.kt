package com.hyunakim.gunsiya

import android.app.Application
import com.hyunakim.gunsiya.data.AppContainer
import com.hyunakim.gunsiya.data.AppDataContainer

class GunsiyaApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}