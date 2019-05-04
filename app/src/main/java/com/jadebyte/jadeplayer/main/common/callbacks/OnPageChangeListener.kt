// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.callbacks

import androidx.viewpager.widget.ViewPager

/**
 * Created by Wilberforce on 2019-05-02 at 15:07.
 */
interface OnPageChangeListener: ViewPager.OnPageChangeListener {
    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {}
}