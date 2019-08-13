// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playlist


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.collection.SparseArrayCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jadebyte.jadeplayer.BR
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.common.crossFade
import com.jadebyte.jadeplayer.main.common.callbacks.OnItemClickListener
import com.jadebyte.jadeplayer.main.common.utils.Utils
import com.jadebyte.jadeplayer.main.common.view.BaseAdapter
import com.jadebyte.jadeplayer.main.common.view.BaseFullscreenDialogFragment
import com.jadebyte.jadeplayer.main.songs.Song
import kotlinx.android.synthetic.main.fragment_playlist_songs_editor_dialog.*


class PlaylistSongsEditorDialogFragment : BaseFullscreenDialogFragment(), OnItemClickListener {

    var items = emptyList<Song>()
    lateinit var viewModel: PlaylistSongsEditorViewModel
    private lateinit var playlist: Playlist


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this)[PlaylistSongsEditorViewModel::class.java]
        playlist = arguments!!.getParcelable("playlist")!!
        viewModel.init(playlist.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_playlist_songs_editor_dialog,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeViewModel()
        doneButton.setOnClickListener { editPlaylist() }
    }

    private fun editPlaylist() {
        val animatorSet = progressBar.crossFade(doneButton, 500)
        viewModel.playlistValue.observe(viewLifecycleOwner, Observer {

            if (it) {
                // Updating playlist was successful
                Utils.vibrateAfterAction(activity!!)
                findNavController().popBackStack()
            } else {
                // Updating playlist wasn't successfu;
                if (animatorSet.isRunning) animatorSet.cancel()
                doneButton.crossFade(progressBar, 500, visibility = View.INVISIBLE)
                Toast.makeText(activity, R.string.sth_went_wrong, Toast.LENGTH_SHORT).show()
            }

        })
        viewModel.updatePlaylist()

    }

    private fun observeViewModel() {
        viewModel.data.observe(viewLifecycleOwner, Observer(::updateViews))
    }

    @Suppress("UNCHECKED_CAST")
    private fun updateViews(items: List<Song>) {
        this.items = items
        (songsRV.adapter as BaseAdapter<Song>).updateItems(items)
        updateSelectedCount()
        content.crossFade(largeProgressBar)
    }

    private fun updateSelectedCount() {
        val selectedSongs = items.filter { it.selected }
        dataNum.text =
            resources.getQuantityString(
                R.plurals.numberOfSongsSelected,
                selectedSongs.count(),
                selectedSongs.count()
            )
    }

    private fun setupView() {
        val variables = SparseArrayCompat<Any>()
        variables.put(BR.selectable, true)
        val adapter =
            BaseAdapter(items, activity!!, R.layout.item_song, BR.song, false, this, variables)
        songsRV.adapter = adapter
        songsRV.layoutManager = LinearLayoutManager(activity)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onItemClick(position: Int, sharableView: View?) {
        if (viewModel.reverseSelection(position)) {
            (songsRV.adapter as BaseAdapter<Song>).notifyItemChanged(position)
            updateSelectedCount()
        }
    }

}
