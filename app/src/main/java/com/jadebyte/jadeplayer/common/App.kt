// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.common

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import androidx.appcompat.app.AppCompatDelegate
import com.jadebyte.jadeplayer.BuildConfig
import com.jadebyte.jadeplayer.main.common.utils.BlurKit
import com.jadebyte.jadeplayer.main.injection.component.AppComponent
import com.jadebyte.jadeplayer.main.injection.component.DaggerAppComponent
import com.jadebyte.jadeplayer.main.injection.module.AppModule
import com.jadebyte.jadeplayer.main.injection.module.CommonModule
import com.squareup.leakcanary.LeakCanary
import timber.log.Timber


/**
 * Created by Wilberforce on 27/03/2019 at 22:48.
 */
class App : Application() {
    lateinit var appComponent: AppComponent
        private set

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
                    .permitDiskReads()
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                VmPolicy.Builder()
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


}