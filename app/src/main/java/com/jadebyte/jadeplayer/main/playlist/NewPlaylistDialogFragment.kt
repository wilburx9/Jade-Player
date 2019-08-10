// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playlist

import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.common.GlideApp
import com.jadebyte.jadeplayer.common.crossFade
import com.jadebyte.jadeplayer.common.px
import com.jadebyte.jadeplayer.main.common.callbacks.TextWatcher
import com.jadebyte.jadeplayer.main.common.dataBinding.DataBindingAdapters
import com.jadebyte.jadeplayer.main.common.utils.ImageUtils
import com.jadebyte.jadeplayer.main.common.utils.UriFileUtils
import com.jadebyte.jadeplayer.main.common.view.BaseFullscreenDialogFragment
import kotlinx.android.synthetic.main.fragment_new_playlist_dialog.*
import kotlinx.coroutines.*
import timber.log.Timber


class NewPlaylistDialogFragment : BaseFullscreenDialogFragment(), View.OnClickListener {
    private val permissionRequestExternalStorage = 0
    private val imageRequestCode = 1
    private var tempThumbUri: Uri? = null
    private val job = Job()
    private val scope = CoroutineScope(job + Dispatchers.Main)

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
        clickableThumbBackground.setOnClickListener(this)
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
            R.id.clickableThumbBackground -> checkForReadStoragePermission()
        }
    }

    private fun checkForReadStoragePermission() {
        val hasStoragePermission = isPermissionGranted(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasStoragePermission) {
            launchImagePickerIntent()
        } else {
            requestPermissions(
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                permissionRequestExternalStorage
            )
        }
    }

    private fun launchImagePickerIntent() {
        val pickImageIntent = Intent(Intent.ACTION_GET_CONTENT)
        pickImageIntent.type = "image/*"

        startActivityForResult(pickImageIntent, imageRequestCode)
    }

    private fun validatePlaylistName() {
        val playlistName = playlistNameField.text.toString()
        if (!playlistName.isNullOrEmpty()) {
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

        if (playlistUri?.toString().isNullOrEmpty()) {
            Toast.makeText(activity, R.string.something_went_wrong, Toast.LENGTH_SHORT).show()
        } else {
            saveImagePermanently(ContentUris.parseId(playlistUri))
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
                displayImage()
            }
        }
    }

    private fun displayImage() {
        GlideApp.with(this)
            .load(tempThumbUri)
            .transform(
                MultiTransformation(DataBindingAdapters.centerCrop, RoundedCorners(7.px))
            ).listener(thumbLoadListener)
            .into(playlistArt)
    }

    private fun saveImagePermanently(playlistId: Long) {
        scope.launch {
            withContext(Dispatchers.IO) {
                if (tempThumbUri != null) {
                    Timber.w("1: $tempThumbUri is not null")
                    val path = UriFileUtils.getPathFromUri(activity, tempThumbUri!!)
                    if (path != null) {
                        Timber.w("2: $path is not null")
                        val resultPath = ImageUtils.getImagePathForPlaylist(playlistId, activity)
                        if (resultPath != null) {
                            Timber.w("3: $resultPath is not null")
                            val needed = ImageUtils.resizeImageIfNeeded(path, 300.0, 300.0, 80, resultPath)
                            Timber.w("Saved path is $needed")
                        }
                    }
                }
            }
            findNavController().popBackStack()
        }
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
            unselectedImageGroup.crossFade(playlistArt)
            return true
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            playlistArt.crossFade(unselectedImageGroup)
            return false
        }

    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

}
