// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playback


import android.animation.AnimatorInflater
import android.animation.AnimatorSet
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
import com.jadebyte.jadeplayer.main.common.callbacks.OnPageChangeListener
import com.jadebyte.jadeplayer.main.common.view.BaseFragment
import com.jadebyte.jadeplayer.main.songs.Song
import com.jadebyte.jadeplayer.main.songs.SongsViewModel
import kotlinx.android.synthetic.main.fragment_playback.*

class PlaybackFragment : BaseFragment(), View.OnClickListener {

    lateinit var viewModel: SongsViewModel // Use SongsViewModel. Change later
    lateinit var currentSong: Song
    var items: List<Song>? = null
    lateinit var animatorSet: AnimatorSet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        viewModel = ViewModelProviders.of(this)[SongsViewModel::class.java]
        currentSong = arguments!!.getParcelable("song")!!
        animatorSet = AnimatorInflater.loadAnimator(activity, R.animator.album_art_rotation) as AnimatorSet
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
        updateViewsAsPerSongChange(viewPager.currentItem)
    }

    private fun setupViewViews() {
        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                updateViewsAsPerSongChange(position)
            }

        })
        viewPager.setPageTransformer(true, ZoomOutPageTransformer())
        viewPager.adapter = PlaybackAdapter(items)
        sectionBackButton.setOnClickListener(this)
        nextButton.setOnClickListener(this)
        previousButton.setOnClickListener(this)
        playButton.setOnClickListener(this)
    }

    private fun updateViewsAsPerSongChange(position: Int) {
        items?.get(position)?.let {
            animatorSet.setTarget(viewPager.findViewWithTag(it))
            songArtist.text = it.album.artist
            songTitle.text = it.title
            animatorSet.start()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.sectionBackButton -> findNavController().popBackStack()
            R.id.previousButton -> playPreviousTrack()
            R.id.nextButton -> playNextSong()
            R.id.playButton -> playPauseSong()
        }
    }

    private fun playPauseSong() {
        if (!animatorSet.isStarted) {
            animatorSet.start()
        } else if (animatorSet.isPaused) {
            animatorSet.resume()
        } else {
            animatorSet.pause()
        }
    }

    private fun playNextSong() {
        val currentPos = viewPager.currentItem
        if (currentPos == (items!!.size - 1)) {
            // If the current item is the last, start from afresh
            viewPager.currentItem = 0
            return
        }

        viewPager.setCurrentItem(currentPos + 1, false)
    }

    private fun playPreviousTrack() {
        val currentPos = viewPager.currentItem
        if (currentPos == 0) {
            // If the current item is the first, go to the last item
            viewPager.currentItem = (items!!.size - 1)
            return
        }

        viewPager.setCurrentItem(currentPos - 1, false)
    }


}
