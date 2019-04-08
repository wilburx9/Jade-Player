// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.splash

import android.content.Intent
import android.os.Bundle
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.common.BaseActivity
import com.jadebyte.jadeplayer.onBoarding.OnBoardingActivity

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: How to Determine next activity
        // if user has seen OnBoarding,
        // check if read storage permission has been granted,
        // then show MainActivity if true otherwise show GetStartedActivity
        // If user has not seen onBoarding then show OnBoardingActivity
        val intent = Intent(this, OnBoardingActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}
