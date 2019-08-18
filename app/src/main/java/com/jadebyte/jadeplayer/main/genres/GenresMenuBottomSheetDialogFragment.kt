// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.genres


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.navigation.fragment.findNavController
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.view.BaseMenuBottomSheet

class GenresMenuBottomSheetDialogFragment : BaseMenuBottomSheet() {

    lateinit var genre: Genre
    @IdRes var popUpTo: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        genre = arguments!!.getParcelable("genre")
        popUpTo = arguments!!.getInt("popUpTo")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_genres_menu_bottom_sheet_dialog, container, false)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.play -> play()
            R.id.playNext -> playNext()
            R.id.addToPlayList -> addToPlayList()
            R.id.share -> share()
        }
    }

    private fun play() {
        findNavController().popBackStack()
    }

    private fun playNext() {
        findNavController().popBackStack()
    }

    private fun addToPlayList() {
        findNavController().popBackStack()
    }

    private fun share() {
        findNavController().popBackStack()
    }
}
