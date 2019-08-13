// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playlist


import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.utils.Utils
import com.jadebyte.jadeplayer.main.common.view.BaseMenuBottomSheet


class PlaylistMenuBottomSheetDialogFragment : BaseMenuBottomSheet() {
    lateinit var playlist: Playlist

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playlist = arguments!!.getParcelable("playlist")!!
    }

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
    }

    private fun deletePlaylist() {
        val builder = AlertDialog.Builder(activity!!)
            .setMessage(activity!!.getString(R.string.playlist_delete_message, playlist.name))
            .setNegativeButton(R.string.no_thanks) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.ok) { dialog, _ ->
                activity?.let {
                    if (!isDetached) {
                        val where = MediaStore.Audio.Playlists._ID + "=?"
                        val whereVal = arrayOf(playlist.id.toString())
                        val rows = it.contentResolver
                            .delete(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, where, whereVal)
                        if (rows > 0) {
                            Toast.makeText(it, getString(R.string.sth_deleted, playlist.name), Toast.LENGTH_SHORT)
                                .show()
                            Utils.vibrateAfterAction(it)
                            dialog.dismiss()
                        } else {
                            Toast.makeText(it, R.string.sth_went_wrong, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                dialog.dismiss()
                findNavController().popBackStack()
            }
        val dialog = builder.create()
        dialog.window?.attributes?.windowAnimations = R.style.AppTheme_DialogAnimation
        dialog.show()
    }

    private fun editPlaylist() {
        findNavController().popBackStack()
    }

    private fun playPlaylistNext() {
        findNavController().popBackStack()
    }

    private fun sharePlaylist() {
        findNavController().popBackStack()
    }

    private fun editSongs() {
        findNavController().navigate(
            PlaylistMenuBottomSheetDialogFragmentDirections
                .actionPlaylistMenuBottomSheetDialogFragmentToPlaylistSongsEditorDialogFragment(playlist)
        )

    }
}




