// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.songs


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.view.BaseMenuBottomSheet


class SongsMenuBottomSheetDialogFragment : BaseMenuBottomSheet() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_songs_menu_bottom_sheet_dialog, container, false)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.share -> shareTrack()
            R.id.playNext -> playNextTrack()
            R.id.favourite -> favouriteTrack()
            R.id.addToPlayList -> addTrackToPlayList()
            R.id.delete -> deleteTrack()
        }
        findNavController().popBackStack()
    }

    private fun deleteTrack() {
       // TODO: Implement
    }

    private fun addTrackToPlayList() {
        // TODO: Implement
    }

    private fun favouriteTrack() {
        // TODO: Implement
    }

    private fun shareTrack() {
        // TODO: Implement
    }

    private fun playNextTrack() {
        // TODO: Implement
    }


}
