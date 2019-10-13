// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.explore


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jadebyte.jadeplayer.BR
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.common.crossFadeWidth
import com.jadebyte.jadeplayer.common.observeOnce
import com.jadebyte.jadeplayer.main.albums.Album
import com.jadebyte.jadeplayer.main.common.callbacks.OnItemClickListener
import com.jadebyte.jadeplayer.main.common.view.BaseAdapter
import kotlinx.android.synthetic.main.fragment_explore.*


class ExploreFragment : Fragment(), OnItemClickListener {

    private var albums: List<Album> = emptyList()
    private var playedList: List<RecentlyPlayed> = emptyList()
    private lateinit var viewModel: ExploreViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this)[ExploreViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_explore, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
        navigationIcon.setOnClickListener(
            Navigation.createNavigateOnClickListener(
                R.id.action_exploreFragment_to_navigationDialogFragment
            )
        )

    }

    @Suppress("UNCHECKED_CAST")
    private fun observeViewModel() {
        // We want to load the random albums before the recently played

        fun observePlayed() {
            viewModel.recentlyPlayed.observe(viewLifecycleOwner, Observer {
                if (playedList.isEmpty()) {
                    // We only want to play the animation the first time a non empty result is returned
                    if (it.isEmpty()) {
                        emptyPlaylist.crossFadeWidth(progressBar)
                        return@Observer
                    } else {
                        val otherView = if (emptyPlaylist.visibility == View.VISIBLE) emptyPlaylist else progressBar
                        playedRV.crossFadeWidth(otherView)
                    }
                }
                playedList = it
                playedList.forEachIndexed { index, played -> played.isPlaying = index == 0 }
                (playedRV.adapter as BaseAdapter<RecentlyPlayed>).updateItems(playedList)
            })
        }

        if (albums.isEmpty()) {
            viewModel.init()
            viewModel.items.observeOnce(viewLifecycleOwner, Observer {
                albums = it
                (randomAlbumsRV.adapter as BaseAdapter<Album>).updateItems(albums)
                observePlayed()
            })
        } else {
            viewModel.overrideCurrentItems(albums)
        }
    }

    private fun setupViews() {
        val albumAdapter = BaseAdapter(
            albums, activity!!, R.layout.item_album, BR.album, this,
            setOf(R.anim.fast_fade_in), true
        )
        randomAlbumsRV.adapter = albumAdapter
        val layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        randomAlbumsRV.layoutManager = layoutManager

        val playedAdapter =
            BaseAdapter(playedList, activity!!, R.layout.item_recently, BR.recentlyPlayed, animSet = null)
        playedRV.adapter = playedAdapter
        playedRV.layoutManager = LinearLayoutManager(activity)
        scrollView.isNestedScrollingEnabled = true
    }

    override fun onItemClick(position: Int, sharableView: View?) {
        val transitionName = ViewCompat.getTransitionName(sharableView!!)!!
        val extras = FragmentNavigator.Extras.Builder()
            .addSharedElement(sharableView, transitionName)
            .build()
        val action =
            ExploreFragmentDirections.actionExploreFragmentToAlbumSongsFragment(albums[position], transitionName)
        findNavController().navigate(action, extras)
    }

    override fun onItemLongClick(position: Int) {
        val action =
            ExploreFragmentDirections.actionExploreFragmentToAlbumsMenuBottomSheetDialogFragment(album = albums[position])
        findNavController().navigate(action)
    }


    override fun onDestroyView() {
        randomAlbumsRV.adapter = null
        playedRV.adapter = null
        super.onDestroyView()
    }
}
