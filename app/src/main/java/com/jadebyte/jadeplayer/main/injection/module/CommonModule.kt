// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.injection.module

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Wilberforce on 10/04/2019 at 16:16.
 *  Module which provides all required commomn dependencies
 */
@Module
// Safe here as we are dealing with Dagger2 module
@Suppress("unused")
class CommonModule {

    /**
     * Provides SharedPreferences object
     * @return the default SharedPreferences file for the entire app
     */
    @Provides
    @Singleton
    internal fun provideSharedPreferences(application: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application)
    }
}