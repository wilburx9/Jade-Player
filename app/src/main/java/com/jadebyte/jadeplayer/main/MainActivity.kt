// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.callbacks.OnNavigationItemClickListener
import com.jadebyte.jadeplayer.main.common.data.Constants
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnNavigationItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigationBar.setupWithNavController(findNavController(R.id.mainNavHostFragment))
    }

    override fun onNavigationItemClicked(itemId: String) {
        when (itemId) {
            Constants.NAV_SONGS -> navigationBar.selectedItemId = R.id.songsFragment
            Constants.NAV_PLAYLIST -> navigationBar.selectedItemId = R.id.playlistFragment
        }
    }
}
