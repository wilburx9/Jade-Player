// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.albums


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.jadebyte.jadeplayer.BR
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.common.App
import com.jadebyte.jadeplayer.databinding.FragmentAlbumSongsBinding
import com.jadebyte.jadeplayer.main.common.callbacks.OnItemClickListener
import com.jadebyte.jadeplayer.main.common.utils.TimeUtils
import com.jadebyte.jadeplayer.main.common.view.BaseAdapter
import com.jadebyte.jadeplayer.main.songs.Song
import kotlinx.android.synthetic.main.fragment_album_songs.*
import java.util.concurrent.TimeUnit

class AlbumSongsFragment : Fragment(), OnItemClickListener {

    lateinit var viewModel: AlbumSongsViewModel
    lateinit var album: Album
    lateinit var adapter: BaseAdapter<Song>
    private var items = emptyList<Song>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        album = arguments!!.getParcelable("album")!!
        App.appComponent.inject(this)
        viewModel = ViewModelProviders.of(this)[AlbumSongsViewModel::class.java]
        viewModel.init(album.id!!)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentAlbumSongsBinding>(
            inflater,
            R.layout.fragment_album_songs,
            container,
            false
        )
        val view = binding.root
        binding.album = album
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setTransitionName(albumArt, arguments!!.getString("transitionName"))
        setupRecyclerView()
        observeViewModel()
        backButton.setOnClickListener { findNavController().popBackStack() }
    }

    private fun observeViewModel() {
        viewModel.data.observe(viewLifecycleOwner, Observer {
            updateViews(it)
        })
    }

    private fun updateViews(items: List<Song>) {
        this.items = items
        albumSongsDuration.text = getTotalTime()
        adapter.updateItems(items)
    }

    private fun getTotalTime(): CharSequence? {
        val secs = TimeUnit.MILLISECONDS.toSeconds(TimeUtils.getTotalSongsDuration(items))
        return getString(
            R.string.two_comma_separated_values,
            resources.getQuantityString(R.plurals.numberOfSongs, items.size, items.size),
            TimeUtils.formatElapsedTime(secs, activity)
        )
    }

    private fun setupRecyclerView() {
        adapter = BaseAdapter(items, activity!!, R.layout.item_album_song, BR.song, itemClickListener = this)
        albumSongsRV.adapter = adapter
        albumSongsRV.layoutManager = LinearLayoutManager(activity!!)
    }

    override fun onItemClick(position: Int, art: ImageView?) {

    }
}
