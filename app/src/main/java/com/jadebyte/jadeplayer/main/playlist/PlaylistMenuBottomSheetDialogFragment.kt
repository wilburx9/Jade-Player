// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playlist


import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.utils.Utils
import com.jadebyte.jadeplayer.main.common.view.BaseMenuBottomSheet


class PlaylistMenuBottomSheetDialogFragment : BaseMenuBottomSheet() {
    lateinit var playlist: Playlist
    @IdRes var popUpTo: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playlist = arguments!!.getParcelable("playlist")!!
        popUpTo = arguments!!.getInt("popUpTo")

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
            R.id.delete -> showDeleteConfirmation()
        }
    }

    private fun showDeleteConfirmation() {
        fun deletePlaylist(dialog: DialogInterface?) {
            val viewModel = ViewModelProviders.of(this)[WritePlaylistViewModel::class.java]

            viewModel.data.observe(viewLifecycleOwner, Observer {
                val activity = activity
                if (activity != null && !isDetached) {
                    val message = if (it.success) {
                        Utils.vibrateAfterAction(activity)
                        getString(R.string.sth_deleted, playlist.name)
                    } else {
                        getString(it.message!!)
                    }
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                }

                dialog?.dismiss()
                findNavController().popBackStack()
            })

            viewModel.deletePlaylist(playlist)
        }

        val builder = AlertDialog.Builder(activity!!)
            .setMessage(activity!!.getString(R.string.playlist_delete_message, playlist.name))
            .setNegativeButton(R.string.no_thanks) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.ok) { dialog, _ -> deletePlaylist(dialog) }

        val dialog = builder.create()
        dialog.window?.attributes?.windowAnimations = R.style.AppTheme_DialogAnimation
        dialog.show()
    }


    private fun editPlaylist() {
        findNavController().navigate(
            PlaylistMenuBottomSheetDialogFragmentDirections
                .actionPlaylistMenuBottomSheetDialogFragmentToWritePlaylistDialogFragment(playlist)
        )
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
                .actionPlaylistMenuBottomSheetDialogFragmentToPlaylistSongsEditorDialogFragment(playlist),
            NavOptions.Builder().setPopUpTo(popUpTo, false).build()
        )

    }
}




