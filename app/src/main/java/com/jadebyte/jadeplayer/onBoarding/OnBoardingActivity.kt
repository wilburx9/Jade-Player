// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.onBoarding

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.core.content.edit
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.common.BaseActivity
import com.jadebyte.jadeplayer.getStarted.GetStartedActivity
import com.jadebyte.jadeplayer.main.MainActivity
import com.jadebyte.jadeplayer.main.common.callbacks.OnPageChangeListener
import kotlinx.android.synthetic.main.activity_on_boarding.*
import org.koin.android.ext.android.inject

class OnBoardingActivity : BaseActivity(), OnPageChangeListener, View.OnClickListener {

    private val preferences: SharedPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)
        viewPager.adapter = OnBoardingAdapter(this, boards)
        viewPager.addOnPageChangeListener(this)
        skipButton.setOnClickListener(this)
        next.setOnClickListener(this)
        gotIt.setOnClickListener(this)
    }


    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        val newProgress = (position + positionOffset) / (boards.size - 1)
        onBoardingRoot.progress = newProgress
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.skipButton, R.id.gotIt -> {
                startNextActivity()
            }
            R.id.next -> {
                viewPager.setCurrentItem(viewPager.currentItem + 1, true)
            }
        }
    }

    private fun startNextActivity() {
        val nextActivity =
            if (isPermissionGranted(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                MainActivity::class.java
            } else {
                GetStartedActivity::class.java
            }

        val intent = Intent(this, nextActivity)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        preferences.edit {
            putBoolean(HAS_SEEN_ON_BOARDING, true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewPager.removeOnPageChangeListener(this)
    }

    companion object {
        const val HAS_SEEN_ON_BOARDING = "com.jadebyte.jadeplayer.onBoarding.hasSeenOnBoarding"
    }

}


private val boards = arrayOf(
    Board(R.drawable.on_boarding1, R.string.board_title_1, R.string.board_description_1),
    Board(R.drawable.on_boarding2, R.string.board_title_2, R.string.board_description_2),
    Board(R.drawable.on_boarding3, R.string.board_title_3, R.string.board_description_3)
)