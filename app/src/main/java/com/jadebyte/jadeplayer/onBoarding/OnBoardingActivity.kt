// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.onBoarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.getStarted.GetStartedActivity
import kotlinx.android.synthetic.main.activity_on_boarding.*

class OnBoardingActivity : AppCompatActivity(), ViewPager.OnPageChangeListener, View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)
        viewPager.adapter = OnBoardingAdapter(this, boards)
        viewPager.addOnPageChangeListener(this)
        skipButton.setOnClickListener(this)
        next.setOnClickListener(this)
        gotIt.setOnClickListener(this)
    }


    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        val newProgress = (position + positionOffset) / (boards.size - 1)
        onBoardingRoot.progress = newProgress
    }

    override fun onPageSelected(position: Int) {}

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.skipButton, R.id.gotIt -> {
                val intent = Intent(this, GetStartedActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
            R.id.next -> {
                viewPager.setCurrentItem(viewPager.currentItem + 1, true)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewPager.removeOnPageChangeListener(this)
    }
}


private val boards = arrayOf(
    Board(R.drawable.on_boarding1, R.string.board_title_1, R.string.board_description_1),
    Board(R.drawable.on_boarding2, R.string.board_title_2, R.string.board_description_2),
    Board(R.drawable.on_boarding3, R.string.board_title_3, R.string.board_description_3)
)