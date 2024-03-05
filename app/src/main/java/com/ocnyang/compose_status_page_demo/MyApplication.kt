package com.ocnyang.compose_status_page_demo

import android.app.Application
import com.ocnyang.status_box.StatusBoxGlobalConfig
import com.ocnyang.status_box.initDef

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // You can use the built-in default configuration
        StatusBoxGlobalConfig.initDef()

        /**
         * Or you can set up a custom page in this way
         */
        //        StatusBoxGlobalConfig.apply {
        //            errorComponent { DefaultErrorStateView() }
        //            loadingComponent { DefaultLoadingStateView() }
        //            emptyComponent { DefaultEmptyStateView() }
        //            initComponent { DefaultInitialStateView() }
        //        }
    }
}