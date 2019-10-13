// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.locator.module

import android.app.Application
import android.content.ComponentName
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.jadebyte.jadeplayer.main.common.data.CloudKeys
import com.jadebyte.jadeplayer.main.common.network.DelegatingSocketFactory
import com.jadebyte.jadeplayer.main.common.network.HttpInterceptor
import com.jadebyte.jadeplayer.main.common.network.image.ImageUrlFetcher
import com.jadebyte.jadeplayer.main.navigation.NavViewModel
import com.jadebyte.jadeplayer.main.playback.MediaSessionConnection
import com.jadebyte.jadeplayer.main.playback.PlaybackService
import com.jadebyte.jadeplayer.main.playback.PlaybackViewModel
import com.jadebyte.jadeplayer.main.search.SearchViewModel
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.concurrent.TimeUnit
import javax.net.SocketFactory


/**
 * Created by Wilberforce on 2019-08-21 at 22:43.
 *  Module which provides all required common dependencies
 */

val commonModule = module {
    single { PreferenceManager.getDefaultSharedPreferences(get()) }
    single { CloudKeys(get<SharedPreferences>()) }
    single { CacheControl.Builder().maxAge(30, TimeUnit.DAYS).build() }
    single { ImageUrlFetcher(get(), get(), get(), get()) }
    single { MediaSessionConnection.getInstance(get(), ComponentName(get(), PlaybackService::class.java), get()) }
    viewModel { NavViewModel(get()) }
    viewModel { PlaybackViewModel(get(), get(), get())}
    viewModel { SearchViewModel(get()) }
    single {
        OkHttpClient.Builder()
            .socketFactory(DelegatingSocketFactory(SocketFactory.getDefault()))
            .cache(Cache(get<Application>().cacheDir, 50 * 1024 * 1024)) // 52.4MB
            .addInterceptor(HttpInterceptor(get(), get()))
            .build()
    }
}