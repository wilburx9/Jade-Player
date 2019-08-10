// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.injection.module

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.jadebyte.jadeplayer.main.common.data.CloudKeys
import com.jadebyte.jadeplayer.main.common.network.DelegatingSocketFactory
import com.jadebyte.jadeplayer.main.common.network.HttpInterceptor
import com.jadebyte.jadeplayer.main.common.network.image.ImageUrlFetcher
import dagger.Module
import dagger.Provides
import dagger.Reusable
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.SocketFactory.getDefault


/**
 * Created by Wilberforce on 10/04/2019 at 16:16.
 *  Module which provides all required commomn dependencies
 */
@Module
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


    /**
     * provides the default app-wide OKHttp Client
     */
    @Provides
    @Singleton
    internal fun getOkHttp(application: Application): OkHttpClient {
        return OkHttpClient.Builder()
            .socketFactory(socketFactory)
            .cache(Cache(application.cacheDir, 50 * 1024 * 1024)) // 52.4MB
            .addInterceptor(HttpInterceptor())
            .build()
    }

    @Provides
    @Reusable
    internal fun getCloudKey(pref: SharedPreferences): CloudKeys = CloudKeys(pref)

    @Provides
    @Singleton
    internal fun getOkHttpCache(): CacheControl = CacheControl.Builder().maxAge(30, TimeUnit.DAYS).build()

    @Provides
    @Singleton
    internal fun getLastFmUrlFetcher(): ImageUrlFetcher = ImageUrlFetcher()

}

val socketFactory = DelegatingSocketFactory(getDefault())