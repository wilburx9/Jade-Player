// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.view

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_songs_menu_bottom_sheet_dialog.*

abstract class BaseMenuBottomSheet: BaseBottomSheetDialogFragment(), View.OnClickListener {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for (i in 0 until buttonsContainer.childCount) {
            buttonsContainer.getChildAt(i).setOnClickListener(this)
        }
    }
}