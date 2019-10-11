// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playback


import android.animation.AnimatorSet
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.common.crossFadeWidth
import com.jadebyte.jadeplayer.databinding.FragmentBottomPlaybackBinding
import com.jadebyte.jadeplayer.main.MainFragmentDirections
import com.jadebyte.jadeplayer.main.common.view.BaseFragment
import kotlinx.android.synthetic.main.fragment_bottom_playback.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class BottomPlaybackFragment : BaseFragment() {

    private val viewModel: PlaybackViewModel by sharedViewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentBottomPlaybackBinding.inflate(inflater, container, false).let {
        it.viewModel = viewModel
        it.lifecycleOwner = this
        return it.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    private fun observeViewModel() {
        var animatorSet: AnimatorSet? = null
        viewModel.playbackState.observe(viewLifecycleOwner, Observer {
            animatorSet?.cancel()
            animatorSet = if (it.isBuffering) {
                progressBar.crossFadeWidth(playButton, visibility = View.INVISIBLE)
            } else {
                playButton.crossFadeWidth(progressBar, visibility = View.INVISIBLE)
            }
        })
    }

    private fun setupViews() {
        playbackSeekBar.setOnTouchListener { _, _ ->
            return@setOnTouchListener true
        }

        clickableView.setOnClickListener {
            viewModel.currentItem.value?.let {
                val transitionName = ViewCompat.getTransitionName(sharableView)!!
                val extras = FragmentNavigator.Extras.Builder().addSharedElement(sharableView, transitionName).build()
                val action = MainFragmentDirections.actionMainFragmentToPlaybackFragment(transitionName)
                activity?.findNavController(R.id.mainNavHostFragment)?.navigate(action, extras)
            }
        }
    }


}
