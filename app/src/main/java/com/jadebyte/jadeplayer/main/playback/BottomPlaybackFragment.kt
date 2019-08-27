// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playback


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.databinding.FragmentBottomPlaybackBinding
import com.jadebyte.jadeplayer.main.MainFragmentDirections
import com.jadebyte.jadeplayer.main.common.view.BaseFragment
import kotlinx.android.synthetic.main.fragment_bottom_playback.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class BottomPlaybackFragment : BaseFragment() {

    private val playbackViewModel: PlaybackViewModel by viewModel()
    private var binding: FragmentBottomPlaybackBinding? = null
    private var mediaItem: MediaItemData? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomPlaybackBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                MainFragmentDirections.actionMainFragmentToPlaybackFragment(mediaItem!!, transitionName)
            activity?.findNavController(R.id.mainNavHostFragment)?.navigate(action, extras)
        }
    }

    private fun observeViewData() {
        playbackViewModel.mediaItems.observe(this, Observer {
            mediaItem = it.first { it.isPlaying }
            binding?.mediaItem = mediaItem
            binding?.executePendingBindings()
        })
    }


    override fun onDestroyView() {
        binding?.unbind()
        binding = null
        super.onDestroyView()
    }

}
