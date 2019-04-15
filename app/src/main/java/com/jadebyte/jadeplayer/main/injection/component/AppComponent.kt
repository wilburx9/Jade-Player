// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.injection.component

import com.jadebyte.jadeplayer.main.injection.module.AppModule
import com.jadebyte.jadeplayer.main.injection.module.CommonModule
import com.jadebyte.jadeplayer.main.navigation.NavigationDialogFragment
import com.jadebyte.jadeplayer.main.navigation.ViewModelModule
import dagger.Component
import javax.inject.Singleton

/**
 * Created by Wilberforce on 10/04/2019 at 16:46.
 */
@Singleton
@Component(modules = [AppModule::class, CommonModule::class, ViewModelModule::class])
interface AppComponent {
    fun inject(fragment: NavigationDialogFragment)
}