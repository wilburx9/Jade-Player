// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playlist

import android.content.ContentValues
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.callbacks.TextWatcher
import com.jadebyte.jadeplayer.main.common.view.BaseFullscreenDialogFragment
import kotlinx.android.synthetic.main.fragment_new_playlist_dialog.*


class NewPlaylistDialogFragment : BaseFullscreenDialogFragment(), View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_playlist_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        closeButton.setOnClickListener(this)
        createPlaylist.setOnClickListener(this)
        playlistNameField.addTextChangedListener(playlistNameTextWatcher)
        playlistNameField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validatePlaylistName()
                return@setOnEditorActionListener true
            }

            return@setOnEditorActionListener false
        }
    }

    private val playlistNameTextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            super.onTextChanged(s, start, before, count)
            createPlaylist.isEnabled = count > 0
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.closeButton -> findNavController().popBackStack()
            R.id.createPlaylist -> validatePlaylistName()
        }
    }

    private fun validatePlaylistName() {
        val playlistName = playlistNameField.text.toString()
        if (!TextUtils.isEmpty(playlistName)) {
            createPlaylist(playlistName)
        } else {
            Toast.makeText(activity, R.string.playlist_empty_message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun createPlaylist(playlist: String) {
        val uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI
        val values = ContentValues(1)
        values.put(MediaStore.Audio.Playlists.NAME, playlist)
        val playlistUri = activity!!.contentResolver.insert(uri, values)

        if (TextUtils.isEmpty(playlistUri?.toString())) {
            Toast.makeText(activity, R.string.something_went_wrong, Toast.LENGTH_SHORT).show()
        } else {
            findNavController().popBackStack()
        }

    }


    override fun onDestroyView() {
        playlistNameField.removeTextChangedListener(playlistNameTextWatcher)
        super.onDestroyView()
    }


}
