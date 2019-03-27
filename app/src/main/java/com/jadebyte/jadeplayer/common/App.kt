// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.common

import android.app.Application
import com.jadebyte.jadeplayer.BuildConfig
import timber.log.Timber

/**
 * Created by Wilberforce on 27/03/2019 at 22:48.
 */
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        plantTimber()
    }

    private fun plantTimber() {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}