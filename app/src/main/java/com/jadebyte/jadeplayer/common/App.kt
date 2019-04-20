// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.common

import android.app.Application
import android.os.StrictMode
import androidx.appcompat.app.AppCompatDelegate
import com.jadebyte.jadeplayer.BuildConfig
import com.jadebyte.jadeplayer.main.common.injection.component.AppComponent
import com.jadebyte.jadeplayer.main.common.injection.component.DaggerAppComponent
import com.jadebyte.jadeplayer.main.common.injection.module.AppModule
import com.jadebyte.jadeplayer.main.common.injection.module.CommonModule
import com.jadebyte.jadeplayer.main.common.utils.BlurKit
import com.squareup.leakcanary.LeakCanary
import timber.log.Timber


/**
 * Created by Wilberforce on 27/03/2019 at 22:48.
 */
class App : Application() {

    override fun onCreate() {
        enableStrictMode()
        super.onCreate()
        initLeakCanary()
        initBlurKit()
        plantTimber()
        setupTheme()
        setupDagger()
    }

    private fun enableStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
        }
    }

    private fun initBlurKit() {
        BlurKit.init(this)
    }

    private fun initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
    }

    private fun setupDagger() {
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .commonModule(CommonModule())
            .build()

    }

    private fun setupTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    private fun plantTimber() {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

    companion object {
        @JvmStatic
        lateinit var appComponent: AppComponent
            private set
    }
}