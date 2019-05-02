// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playback


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.view.BaseFragment
import com.jadebyte.jadeplayer.main.songs.Song
import com.jadebyte.jadeplayer.main.songs.SongsViewModel
import kotlinx.android.synthetic.main.fragment_playback.*

class PlaybackFragment : BaseFragment(), View.OnClickListener {

    lateinit var viewModel: SongsViewModel // Use SongsViewModel. Change later
    lateinit var currentSong: Song
    var items: List<Song>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        viewModel = ViewModelProviders.of(this)[SongsViewModel::class.java]
        currentSong = arguments!!.getParcelable("song")!!
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playback, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setTransitionName(viewPager, arguments!!.getString("transitionName"))
        setupViewViews()
        observeViewModel()
    }

    private fun observeViewModel() {
        if (items.isNullOrEmpty()) {
            viewModel.init()
            viewModel.data.observe(viewLifecycleOwner, Observer(::updateViews))
        } else {
            viewModel.data.value = items
        }
    }

    private fun updateViews(items: List<Song>?) {
        this.items = items
        (viewPager.adapter as PlaybackAdapter).updateItems(items)
    }

    private fun setupViewViews() {
        viewPager.adapter = PlaybackAdapter(items)
        sectionBackButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.sectionBackButton -> findNavController().popBackStack()
        }
    }



}
