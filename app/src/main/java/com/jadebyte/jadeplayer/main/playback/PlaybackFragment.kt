// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playback


import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.os.Handler
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.common.*
import com.jadebyte.jadeplayer.main.common.callbacks.AnimatorListener
import com.jadebyte.jadeplayer.main.common.callbacks.OnPageChangeListener
import com.jadebyte.jadeplayer.main.common.view.BaseFragment
import com.jadebyte.jadeplayer.main.common.view.ZoomOutPageTransformer
import com.jadebyte.jadeplayer.main.songs.Song
import kotlinx.android.synthetic.main.fragment_playback.*
import java.util.concurrent.TimeUnit


class PlaybackFragment : BaseFragment(), View.OnClickListener, View.OnTouchListener {

    private lateinit var viewModel: PlaybackViewModel
    private lateinit var currentSong: Song
    private var items = emptyList<Song>()
    private lateinit var rotationAnimSet: AnimatorSet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
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
        viewModel = ViewModelProviders.of(activity!!)[PlaybackViewModel::class.java]
        setupView()
        observeViewData()
    }

    private fun observeViewData() {
        viewModel.mediatorLiveData.observe(viewLifecycleOwner, dataObserver)
    }


    private val dataObserver = Observer<Any> {
        if (it is Int) {
            viewPager.setCurrentItem(it, false)
        } else {
            @Suppress("UNCHECKED_CAST")
            updateViews(it as List<Song>)
        }
    }

    private fun updateViews(items: List<Song>) {
        this.items = items
        (viewPager.adapter as PlaybackAdapter).updateItems(items)
        updateViewsAsPerSongChange(viewPager.currentItem)
    }

    private fun setupView() {
        rotationAnimSet = AnimatorInflater.loadAnimator(activity, R.animator.album_art_rotation) as AnimatorSet
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
        lyricsButton.setOnClickListener(this)
        closeButton.setOnClickListener(this)
        moreOptions.setOnClickListener(this)
        viewPager.setOnTouchListener(this)
        playingTracks.setOnClickListener(this)
    }

    private fun updateViewsAsPerSongChange(position: Int) {
        items[position].let {
            rotationAnimSet.setTarget(viewPager.findViewWithTag(it))
            songArtist.setText(it.album.artist)
            songTitle.setText(it.title)
            rotationAnimSet.start()
            totalDuration.text = DateUtils.formatElapsedTime(TimeUnit.MILLISECONDS.toSeconds(it.duration))
            // TODO: Remove the below line when songs start playing
            countdownDuration.text = DateUtils.formatElapsedTime(TimeUnit.MILLISECONDS.toSeconds(0))
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.sectionBackButton -> findNavController().popBackStack()
            R.id.previousButton -> playPreviousTrack()
            R.id.nextButton -> playNextSong()
            R.id.playButton -> playPauseSong()
            R.id.lyricsButton -> showFindingLyrics()
            R.id.closeButton -> closeLyrics()
            R.id.moreOptions -> showMenuBottomSheet()
            R.id.playingTracks -> showCurrentTracks()
        }
    }

    private fun showCurrentTracks() {
        if ((playingTracks.drawable as Animatable).isRunning) {
            return
        }

        val fragment = childFragmentManager.findFragmentByTag("CurrentSongsFragment")
        val animDrawable = AnimatedVectorDrawableCompat.create(
            activity!!,
            if (fragment != null) R.drawable.anim_close_to_playlist_current else R.drawable.anim_playlist_current_to_close
        )
        playingTracks.setImageDrawable(animDrawable)
        (playingTracks.drawable as Animatable).start()
        if (fragment == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.currentSongsContainer, CurrentSongsFragment(), "CurrentSongsFragment").commit()
        } else {
            childFragmentManager.beginTransaction().remove(fragment).commit()
        }

    }

    private fun showMenuBottomSheet() {
        val action = PlaybackFragmentDirections.actionPlaybackFragmentToSongsMenuBottomSheetDialogFragment( items[viewPager.currentItem])
        findNavController().navigate(action)
    }

    private fun closeLyrics() {
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            viewPager.fadeInSlideDown(translationY, slideDuration),
            lyricsButton.fadeInSlideDown(translationY, slideDuration),
            closeButton.fadeOutSlideDown(translationY, slideDuration),
            quoteImg.fadeOutSlideDown(translationY, slideDuration),
            lyricsText.fadeOutSlideDown(translationY, slideDuration),
            lyricsSource.fadeOutSlideDown(translationY, slideDuration)
        )
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.addListener(object : AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                albumArtGroup.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator?) {
                lyricsGroup.visibility = View.GONE
                animatorSet.removeAllListeners()
            }
        })

        animatorSet.start()
    }

    private fun showFindingLyrics() {
        if (hasLyrics()) {
            showFoundLyrics()
            return
        }
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            viewPager.fadeOutSlideUp(translationY, slideDuration),
            lyricsButton.fadeOutSlideUp(translationY, slideDuration),
            progressBar.fadeInSlideUp(translationY, slideDuration),
            findingLyrics.fadeInSlideUp(translationY, slideDuration)
        )
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.addListener(object : AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                loadingLyricsGroup.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator?) {
                albumArtGroup.visibility = View.GONE
                Handler().postDelayed({
                    showFoundLyrics(getString(R.string.dummyLyrics), getString(R.string.dummyLyricsSource))
                }, TimeUnit.SECONDS.toMillis(4))
                animatorSet.removeAllListeners()
            }
        })

        animatorSet.start()
    }

    private fun showFoundLyrics(lyrics: String? = null, source: String? = null) {
        val lyricsIsEmpty = lyrics.isNullOrEmpty()
        if (!lyricsIsEmpty) {
            lyricsText.text = lyrics
            lyricsSource.text = source
        }

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            if (!lyricsIsEmpty) progressBar.fadeOutSlideUp(translationY, slideDuration) else
                viewPager.fadeOutSlideUp(translationY, slideDuration),
            if (!lyricsIsEmpty) findingLyrics.fadeOutSlideUp(translationY, slideDuration) else
                lyricsButton.fadeOutSlideUp(translationY, slideDuration),
            closeButton.fadeInSlideUp(translationY, slideDuration),
            quoteImg.fadeInSlideUp(translationY, slideDuration),
            lyricsText.fadeInSlideUp(translationY, slideDuration),
            lyricsSource.fadeInSlideUp(translationY, slideDuration)
        )

        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.addListener(object : AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                lyricsGroup.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator?) {
                if (!lyricsIsEmpty) loadingLyricsGroup.visibility = View.GONE else albumArtGroup.visibility = View.GONE
                animatorSet.removeAllListeners()
            }
        })
        animatorSet.start()
    }

    private fun playPauseSong() {
        if (!rotationAnimSet.isStarted) {
            rotationAnimSet.start()
        } else if (rotationAnimSet.isPaused) {
            rotationAnimSet.resume()
        } else {
            rotationAnimSet.pause()
        }
    }

    private fun playNextSong() {
        val currentPos = viewPager.currentItem
        if (currentPos == (items.size - 1)) {
            // If the current item is the last, start from afresh
            viewModel.updateIndexOfPlayingSong(0)
            return
        }

        viewModel.updateIndexOfPlayingSong(currentPos + 1)
    }


    private fun playPreviousTrack() {
        val currentPos = viewPager.currentItem
        if (currentPos == 0) {
            // If the current item is the first, go to the last item
            viewModel.updateIndexOfPlayingSong(items.size - 1)
            return
        }

        viewModel.updateIndexOfPlayingSong(currentPos - 1)
    }

    private fun hasLyrics(): Boolean {
        return !lyricsText.text.isNullOrEmpty()
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return when (v?.id) {
            R.id.viewPager -> handleViewPagerTouch(event)
            else -> {
                v?.performClick()
                false
            }
        }
    }

    private fun handleViewPagerTouch(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP || event?.action == MotionEvent.ACTION_CANCEL) {
            if (rotationAnimSet.isStarted && rotationAnimSet.isPaused) {
                rotationAnimSet.resume()
            }
        } else {
            if (rotationAnimSet.isStarted && !rotationAnimSet.isPaused) {
                rotationAnimSet.pause()
            }
        }
        return false
    }

    override fun onDestroyView() {
        viewModel.mediatorLiveData.removeObserver(dataObserver)
        rotationAnimSet.cancel()
        super.onDestroyView()
    }


}

private const val slideDuration = 600L
private val translationY = 110F.px
