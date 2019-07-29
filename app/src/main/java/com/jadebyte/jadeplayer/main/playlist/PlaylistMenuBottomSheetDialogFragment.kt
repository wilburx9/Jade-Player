// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playlist


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.view.BaseMenuBottomSheet

class PlaylistMenuBottomSheetDialogFragment : BaseMenuBottomSheet() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlist_menu_bottom_sheet_dialog, container, false)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.share -> sharePlaylist()
            R.id.playNext -> playPlaylistNext()
            R.id.editSongs -> editSongs()
            R.id.editPlaylist -> editPlaylist()
            R.id.delete -> deletePlaylist()
        }
        dismiss()
    }

    private fun deletePlaylist() {
        // TODO: Implement
    }

    private fun editPlaylist() {
        // TODO: Implement
    }

    private fun playPlaylistNext() {
        // TODO: Implement
    }

    private fun sharePlaylist() {
        // TODO: Implement
    }

    private fun editSongs() {
        // TODO: Implement
    }

}
