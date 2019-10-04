// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playback


import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.os.Handler
import android.support.v4.media.session.PlaybackStateCompat
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import androidx.viewpager.widget.ViewPager
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.common.*
import com.jadebyte.jadeplayer.main.common.callbacks.AnimatorListener
import com.jadebyte.jadeplayer.main.common.callbacks.OnPageChangeListener
import com.jadebyte.jadeplayer.main.common.view.BaseFragment
import com.jadebyte.jadeplayer.main.common.view.ZoomOutPageTransformer
import kotlinx.android.synthetic.main.fragment_playback.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.concurrent.TimeUnit


class PlaybackFragment : BaseFragment(), View.OnClickListener, View.OnTouchListener {

    private var userScrollChange = false
    private var previousState: Int = ViewPager.SCROLL_STATE_IDLE
    private val viewModel: PlaybackViewModel by viewModel()
    private var items = emptyList<MediaItemData>()
    private lateinit var rotationAnimSet: AnimatorSet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
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
        setupView()
        observeViewData()
    }

    private fun observeViewData() {
        viewModel.mediaItems.observe(viewLifecycleOwner, Observer { items ->
            if (items.isEmpty()) return@Observer
            this.items = items
            (viewPager.adapter as PlaybackAdapter).updateItems(this.items)
        })

        viewModel.currentItem.observe(viewLifecycleOwner, Observer {
            val index = items.indexOf(it)
            if (index < 0 || index > items.size) return@Observer
            viewPager.setCurrentItem(index, true)
            updateViews(it)
        })

        viewModel.mediaPosition.observe(viewLifecycleOwner, Observer {
            countdownDuration.text = DateUtils.formatElapsedTime(TimeUnit.MILLISECONDS.toSeconds(it))
            playbackSeekBar.progress = it.toInt()
        })

        viewModel.playbackState.observe(viewLifecycleOwner, Observer { updateState(it) })
    }


    private fun setupView() {
        rotationAnimSet = AnimatorInflater.loadAnimator(activity, R.animator.album_art_rotation) as AnimatorSet
        viewPager.adapter = PlaybackAdapter(items)
        viewPager.addOnPageChangeListener(onPageChangeCallback)
        viewPager.setPageTransformer(true, ZoomOutPageTransformer())
        sectionBackButton.setOnClickListener(this)
        nextButton.setOnClickListener(this)
        previousButton.setOnClickListener(this)
        playPauseButton.setOnClickListener(this)
        lyricsButton.setOnClickListener(this)
        closeButton.setOnClickListener(this)
        moreOptions.setOnClickListener(this)
        viewPager.setOnTouchListener(this)
        playingTracks.setOnClickListener(this)
    }

    private fun updateViews(item: MediaItemData?) {
        item?.let {
            val view = viewPager.findViewWithTag<View>(it.id)
            rotationAnimSet.setTarget(view)
            songArtist.setText(it.subtitle)
            songTitle.setText(it.title)
            playbackSeekBar.max = item.duration.toInt()
            totalDuration.text = DateUtils.formatElapsedTime(TimeUnit.MILLISECONDS.toSeconds(it.duration))
        }
    }

    private fun updateState(state: PlaybackStateCompat) {
        if (state.isPlayingOrBuffering) {
            if (playPauseButton.currentView != pauseButton) playPauseButton.showNext()
            if (!rotationAnimSet.isStarted) rotationAnimSet.start() else rotationAnimSet.resume()
        } else {
            rotationAnimSet.pause()
            if (playPauseButton.currentView != playButton) playPauseButton.showPrevious()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.sectionBackButton -> findNavController().popBackStack()
            R.id.previousButton -> viewModel.skipToPrevious()
            R.id.nextButton -> viewModel.skipToNext()
            R.id.playPauseButton -> viewModel.playCurrent()
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
        val action =
            PlaybackFragmentDirections.actionPlaybackFragmentToSongsMenuBottomSheetDialogFragment(items[viewPager.currentItem])
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


    private fun hasLyrics() = !lyricsText.text.isNullOrEmpty()

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

    private val onPageChangeCallback = object : OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            if (userScrollChange) {
                Timber.i("onPageSelected: $position")
                val index = items.indexOf(viewModel.currentItem.value)
                if (index < 0 || index > items.size) return
                if (position > index) {
                    viewModel.skipToNext()
                } else {
                    viewModel.skipToPrevious()
                }
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            if (previousState == ViewPager.SCROLL_STATE_DRAGGING
                && state == ViewPager.SCROLL_STATE_SETTLING
            )
                userScrollChange = true
            else if (previousState == ViewPager.SCROLL_STATE_SETTLING
                && state == ViewPager.SCROLL_STATE_IDLE
            )
                userScrollChange = false

            previousState = state
        }
    }

    override fun onDestroyView() {
        rotationAnimSet.cancel()
        viewPager.removeOnPageChangeListener(onPageChangeCallback)
        super.onDestroyView()
    }


}

private const val slideDuration = 600L
private val translationY = 110F.px
