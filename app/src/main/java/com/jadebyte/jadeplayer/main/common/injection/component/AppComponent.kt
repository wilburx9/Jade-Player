// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.injection.component

import com.jadebyte.jadeplayer.main.albums.AlbumSongsFragment
import com.jadebyte.jadeplayer.main.common.injection.module.AppModule
import com.jadebyte.jadeplayer.main.common.injection.module.CommonModule
import com.jadebyte.jadeplayer.main.common.network.HttpInterceptor
import com.jadebyte.jadeplayer.main.common.network.image.ImageUrlFetcher
import com.jadebyte.jadeplayer.main.common.network.image.album.AlbumModelLoader
import com.jadebyte.jadeplayer.main.common.network.image.artist.ArtistModelLoader
import com.jadebyte.jadeplayer.main.common.network.image.playlist.PlaylistModelLoader
import com.jadebyte.jadeplayer.main.navigation.NavRepository
import com.jadebyte.jadeplayer.splash.SplashActivity
import dagger.Component
import javax.inject.Singleton

/**
 * Created by Wilberforce on 10/04/2019 at 16:46.
 */
@Singleton
@Component(modules = [AppModule::class, CommonModule::class])
interface AppComponent {
    fun inject(interceptor: HttpInterceptor)
    fun inject(modelLoader: ArtistModelLoader)
    fun inject(modelLoader: AlbumModelLoader)
    fun inject(modelLoader: PlaylistModelLoader)
    fun inject(navRepository: NavRepository)
    fun inject(albumSongsFragment: AlbumSongsFragment)
    fun inject(urlFetcher: ImageUrlFetcher)
    fun inject(playlistDataFetcher: PlaylistModelLoader.PlaylistDataFetcher)
    fun inject(splashActivity: SplashActivity)
}