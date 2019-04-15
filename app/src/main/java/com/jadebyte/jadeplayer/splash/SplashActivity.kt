// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.splash

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.common.BaseActivity
import com.jadebyte.jadeplayer.getStarted.GetStartedActivity
import com.jadebyte.jadeplayer.main.MainActivity
import com.jadebyte.jadeplayer.onBoarding.OnBoardingActivity

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val nextActivity =
            if (!preferences.getBoolean(OnBoardingActivity.HAS_SEEN_ON_BOARDING, false)) {
                OnBoardingActivity::class.java
            } else if (!isPermissionGranted(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                GetStartedActivity::class.java
            } else {
                MainActivity::class.java
            }
        val intent = Intent(this, nextActivity)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}
