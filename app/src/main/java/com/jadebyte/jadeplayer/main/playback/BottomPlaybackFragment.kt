// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playback


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.databinding.FragmentBottomPlaybackBinding
import com.jadebyte.jadeplayer.main.MainFragmentDirections
import com.jadebyte.jadeplayer.main.common.view.BaseFragment
import com.jadebyte.jadeplayer.main.songs.Song
import kotlinx.android.synthetic.main.fragment_bottom_playback.*

class BottomPlaybackFragment : BaseFragment() {

    lateinit var viewModel: PlaybackViewModel
    lateinit var binding: FragmentBottomPlaybackBinding
    private var items = emptyList<Song>()
    var indexOfPlayingSong = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!)[PlaybackViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomPlaybackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!)[PlaybackViewModel::class.java]
        setupViews()
        observeViewData()
    }

    private fun setupViews() {
        playbackSeekBar.setOnTouchListener { _, _ ->
            return@setOnTouchListener true
        }

        clickableView.setOnClickListener {
            val transitionName = ViewCompat.getTransitionName(sharableView)!!
            val extras = FragmentNavigator.Extras.Builder().addSharedElement(sharableView, transitionName).build()
            val action =
                MainFragmentDirections.actionMainFragmentToPlaybackFragment(items[indexOfPlayingSong], transitionName)
            activity?.findNavController(R.id.mainNavHostFragment)?.navigate(action, extras)
        }
    }

    private fun observeViewData() {
        viewModel.mediatorLiveData.observe(viewLifecycleOwner, dataObserver)
    }


    @Suppress("UNCHECKED_CAST")
    private val dataObserver = Observer<Any> {
        if (it is Int) {
            indexOfPlayingSong = it
        } else {
            items = it as List<Song>
        }

        if (items.isNotEmpty()) {
            binding.song = items[indexOfPlayingSong]
            binding.executePendingBindings()
        }
    }


    override fun onDestroyView() {
        viewModel.mediatorLiveData.removeObserver(dataObserver)
        binding.unbind()
        super.onDestroyView()
    }

}
