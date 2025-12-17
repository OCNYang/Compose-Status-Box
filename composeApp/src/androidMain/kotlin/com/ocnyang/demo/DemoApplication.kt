package com.ocnyang.demo

import android.app.Application
import com.ocnyang.status_box.StatusBoxGlobalConfig
import com.ocnyang.status_box.initDef

class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize StatusBox global configuration
        StatusBoxGlobalConfig.initDef()
    }
}
