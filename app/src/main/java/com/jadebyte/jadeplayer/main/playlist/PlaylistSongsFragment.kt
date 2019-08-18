// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playlist


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.jadebyte.jadeplayer.BR
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.databinding.FragmentPlaylistSongsBinding
import com.jadebyte.jadeplayer.main.common.callbacks.OnItemClickListener
import com.jadebyte.jadeplayer.main.common.view.BaseAdapter
import com.jadebyte.jadeplayer.main.common.view.BaseFragment
import com.jadebyte.jadeplayer.main.songs.Song
import kotlinx.android.synthetic.main.fragment_playlist_songs.*

class PlaylistSongsFragment : BaseFragment(), OnItemClickListener, View.OnClickListener {

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
        binding = FragmentPlaylistSongsBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.playlist = playlist
        binding.lifecycleOwner = viewLifecycleOwner
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setTransitionName(playlistArt, arguments!!.getString("transitionName"))
        setupViews()
        observeViewModel()
    }

    private fun observeViewModel() {
        songsViewModel.items.observe(viewLifecycleOwner, Observer {
            updateViews(it)
        })
        playlistViewModel.items.observe(viewLifecycleOwner, Observer {
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
        playlistSongsDuration.text = getSongsTotalTime(items)
        (playlistSongsRV.adapter as BaseAdapter<Song>).updateItems(items)
    }

    private fun setupViews() {
        val adapter = BaseAdapter(items, activity!!, R.layout.item_model_song, BR.song, itemClickListener = this)
        playlistSongsRV.adapter = adapter
        playlistSongsRV.layoutManager = LinearLayoutManager(activity!!)
        sectionBackButton.setOnClickListener(this)
        moreOptions.setOnClickListener(this)
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
