// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playlist


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.jadebyte.jadeplayer.databinding.FragmentPlaylistSongsBinding
import com.jadebyte.jadeplayer.main.common.callbacks.OnItemClickListener
import com.jadebyte.jadeplayer.main.common.utils.TimeUtils
import com.jadebyte.jadeplayer.main.common.view.BaseAdapter
import com.jadebyte.jadeplayer.main.songs.Song
import kotlinx.android.synthetic.main.fragment_playlist_songs.*
import java.util.concurrent.TimeUnit

class PlaylistSongsFragment : Fragment(), OnItemClickListener, View.OnClickListener {

    private lateinit var binding: FragmentPlaylistSongsBinding
    private lateinit var songsViewModel: PlaylistSongsViewModel
    private lateinit var playlistViewModel: PlaylistViewModel
    private lateinit var playlist: Playlist
    private var items = emptyList<Song>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        playlist = arguments!!.getParcelable("playlist")!!
        songsViewModel = ViewModelProviders.of(this)[PlaylistSongsViewModel::class.java]
        playlistViewModel = ViewModelProviders.of(this)[PlaylistViewModel::class.java]
        songsViewModel.init(playlist.id)
        playlistViewModel.init(playlist.id)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_playlist_songs,
            container,
            false
        )
        val view = binding.root
        binding.playlist = playlist
        binding.lifecycleOwner = viewLifecycleOwner
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setTransitionName(playlistArt, arguments!!.getString("transitionName"))
        setupRecyclerView()
        observeViewModel()
        sectionBackButton.setOnClickListener(this)
        moreOptions.setOnClickListener(this)
    }

    private fun observeViewModel() {
        songsViewModel.data.observe(viewLifecycleOwner, Observer {
            updateViews(it)
        })
        playlistViewModel.data.observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {
                findNavController().popBackStack()
            } else {
                playlist = it.first()
                binding.playlist = Playlist(playlist)
            }
        })
    }

    @Suppress("UNCHECKED_CAST")
    private fun updateViews(items: List<Song>) {
        this.items = items
        playlistSongsDuration.text = getTotalTime()
        (playlistSongsRV.adapter as BaseAdapter<Song>).updateItems(items)
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
        val adapter = BaseAdapter(items, activity!!, R.layout.item_playlist_song, BR.song, itemClickListener = this)
        playlistSongsRV.adapter = adapter
        playlistSongsRV.layoutManager = LinearLayoutManager(activity!!)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.sectionBackButton -> findNavController().popBackStack()
            R.id.moreOptions -> findNavController().navigate(
                PlaylistSongsFragmentDirections
                    .actionPlaylistSongsFragmentToPlaylistMenuBottomSheetDialogFragment(playlist = playlist)
            )
        }
    }


    override fun onItemClick(position: Int, sharableView: View?) {

    }


}
