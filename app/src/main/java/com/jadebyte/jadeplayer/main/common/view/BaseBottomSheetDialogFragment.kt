// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.view

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jadebyte.jadeplayer.R



/**
 * Created by Wilberforce on 2019-05-14 at 15:25.
 */
open class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {
    // TODO: Fix bottom sheet wrong elements colors in day theme
    override fun getTheme(): Int = R.style.BottomSheetTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window!!.attributes.windowAnimations = R.style.BottomSheetTheme
    }
}