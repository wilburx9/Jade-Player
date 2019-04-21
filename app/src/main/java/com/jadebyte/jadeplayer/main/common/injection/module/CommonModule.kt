// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.injection.module

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.core.content.edit
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jadebyte.jadeplayer.main.common.data.CloudKeys
import com.jadebyte.jadeplayer.main.common.data.Constants
import com.jadebyte.jadeplayer.main.common.network.DelegatingSocketFactory
import com.jadebyte.jadeplayer.main.common.network.HttpInterceptor
import dagger.Module
import dagger.Provides
import dagger.Reusable
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import timber.log.Timber
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
    internal fun getCloudKey(pref: SharedPreferences): CloudKeys {
        var cloudKeys = CloudKeys(
            acrHost = pref.getString(Constants.acrHost, ""),
            acrKey = pref.getString(Constants.acrKey, ""),
            acrKeyFile = pref.getString(Constants.acrKeyFile, ""),
            acrSecret = pref.getString(Constants.acrSecret, ""),
            acrSecretFile = pref.getString(Constants.acrSecretFile, ""),
            lastFmKey = pref.getString(Constants.lastFmKey, ""),
            spotifyClientId = pref.getString(Constants.spotifyClientId, ""),
            spotifySecret = pref.getString(Constants.spotifySecret, "")
        )
        Timber.w("getCloudKey Before: $cloudKeys")
        Firebase.firestore.collection("properties").document("keys").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val result = task.result!!.toObject(CloudKeys::class.java)
                result?.let {
                    cloudKeys = it
                    pref.edit {
                        putString(Constants.acrHost, it.acrHost)
                        putString(Constants.acrKey, it.acrKey)
                        putString(Constants.acrKeyFile, it.acrKeyFile)
                        putString(Constants.acrSecret, it.acrSecret)
                        putString(Constants.acrSecretFile, it.acrSecretFile)
                        putString(Constants.lastFmKey, it.lastFmKey)
                        putString(Constants.spotifyClientId, it.spotifyClientId)
                        putString(Constants.spotifySecret, it.spotifySecret)
                    }

                    Timber.w("getCloudKey After: $cloudKeys")
                    Timber.i("getCloudKey it: $it")
                    Timber.i("getCloudKey After: $result")
                }
            }
        }
        return cloudKeys
    }

    @Provides
    @Singleton
    internal fun getOkHttpCache(): CacheControl{
        return CacheControl.Builder().maxAge(30, TimeUnit.DAYS).build()
    }

}

val socketFactory = DelegatingSocketFactory(getDefault())