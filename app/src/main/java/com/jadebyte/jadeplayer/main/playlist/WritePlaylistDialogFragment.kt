// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playlist

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.common.GlideApp
import com.jadebyte.jadeplayer.common.crossFadeWidth
import com.jadebyte.jadeplayer.common.px
import com.jadebyte.jadeplayer.main.common.callbacks.TextWatcher
import com.jadebyte.jadeplayer.main.common.dataBinding.DataBindingAdapters
import com.jadebyte.jadeplayer.main.common.utils.ImageUtils
import com.jadebyte.jadeplayer.main.common.utils.Utils
import com.jadebyte.jadeplayer.main.common.view.BaseFullscreenDialogFragment
import kotlinx.android.synthetic.main.fragment_write_playlist_dialog.*
import java.io.File


class WritePlaylistDialogFragment : BaseFullscreenDialogFragment(), View.OnClickListener {
    private val permissionRequestExternalStorage = 0
    private val imageRequestCode = 1
    private var tempThumbUri: Uri? = null
    private var playlist: Playlist? = null
    lateinit var viewModel: WritePlaylistViewModel
    var deleteImageFile = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playlist = arguments?.getParcelable("playlist")
        viewModel = ViewModelProviders.of(this)[WritePlaylistViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_write_playlist_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        closeButton.setOnClickListener(this)
        writePlaylist.setOnClickListener(this)
        clickableThumbBackground.setOnClickListener(this)
        removePicture.setOnClickListener(this)
        playlistNameField.addTextChangedListener(playlistNameTextWatcher)
        playlistNameField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                writePlaylist()
                return@setOnEditorActionListener true
            }

            return@setOnEditorActionListener false
        }

        if (playlist != null) {
            writePlaylist.setText(R.string.save_changes)
            playlistNameField.setText(playlist!!.name)
            displayImage(Playlist(playlist!!).modForViewWidth(getPlaylistArtWidth()))
        }
    }

    fun observeViewModel() {
        viewModel.data.observe(viewLifecycleOwner, Observer {
            if (it.success || it.message == null) {
                Utils.vibrateAfterAction(activity)
                findNavController().popBackStack()
            } else {
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private val playlistNameTextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            super.onTextChanged(s, start, before, count)
            enableWriteButton()
        }
    }

    private fun enableWriteButton() {
        writePlaylist.isEnabled = validateData(false)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.closeButton -> findNavController().popBackStack()
            R.id.writePlaylist -> writePlaylist()
            R.id.clickableThumbBackground -> checkForReadStoragePermission()
            R.id.removePicture -> removeSelectedImage()
        }
    }

    private fun removeSelectedImage() {
        tempThumbUri = null
        deleteImageFile = true
        enableWriteButton()
        displayImage(null)
    }

    private fun checkForReadStoragePermission() {
        val hasStoragePermission = isPermissionGranted(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasStoragePermission) {
            launchImagePickerIntent()
        } else {
            requestPermissions(
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                permissionRequestExternalStorage
            )
        }
    }

    private fun launchImagePickerIntent() {
        val pickImageIntent = Intent(Intent.ACTION_GET_CONTENT)
        pickImageIntent.type = "image/*"

        startActivityForResult(pickImageIntent, imageRequestCode)
    }


    private fun validateData(showToast: Boolean = true): Boolean {
        val playlistName = playlistNameField.text.toString()
        @StringRes var res: Int? = null
        if (playlistName.trim().isEmpty()) {
            res = R.string.playlist_empty_message
        } else if (playlist != null) {
            if (tempThumbUri == null && playlistName.trim() == playlist!!.name && !deleteImageFile) {
                res = R.string.playlist_empty_thumb_message
            }
        }

        if (res == null) return true

        if (showToast) Toast.makeText(activity, res, Toast.LENGTH_SHORT).show()

        return false
    }

    private fun writePlaylist() {
        if (!validateData()) {
            return
        }
        val playlistName = playlistNameField.text.toString().trim()
        if (playlist == null) {
            viewModel.createPlaylist(playlistName, tempThumbUri)
        } else {
            viewModel.editPlaylist(playlistName, playlist!!, tempThumbUri, deleteImageFile)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == permissionRequestExternalStorage) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission request was granted
                launchImagePickerIntent()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == imageRequestCode && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            if (uri != null) {
                tempThumbUri = uri
                displayImage(tempThumbUri!!)
            }
            enableWriteButton()
        }
    }

    private fun displayImage(any: Any?) {
        GlideApp.with(this)
            .load(any)
            .transform(
                MultiTransformation(DataBindingAdapters.centerCrop, RoundedCorners(7.px))
            ).listener(thumbLoadListener)
            .into(playlistArt)
    }


    override fun onDestroyView() {
        playlistNameField.removeTextChangedListener(playlistNameTextWatcher)
        super.onDestroyView()
    }


    private val thumbLoadListener = object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            unselectedImageGroup.crossFadeWidth(playlistArt)
            if (removePicture.visibility == View.VISIBLE || uploadPicture.visibility != View.VISIBLE) {
                uploadPicture.crossFadeWidth(removePicture)
            }
            return true
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            playlistArt.crossFadeWidth(unselectedImageGroup, visibility = View.INVISIBLE)
            // Check if tt was loaded from a selected image
            if (model is Uri
                || (playlist != null
                        && File(ImageUtils.getImagePathForModel(playlist!!, activity)).exists())
            ) {
                removePicture.crossFadeWidth(uploadPicture, visibility = View.INVISIBLE)
            }
            return false
        }

    }

    private fun getPlaylistArtWidth(): Int = resources.getDimension(R.dimen.write_playlist_thumb_width).toInt()


}
