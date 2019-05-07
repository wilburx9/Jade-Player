// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playback


import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.common.*
import com.jadebyte.jadeplayer.main.common.callbacks.AnimatorListener
import com.jadebyte.jadeplayer.main.common.callbacks.OnPageChangeListener
import com.jadebyte.jadeplayer.main.common.view.BaseFragment
import com.jadebyte.jadeplayer.main.songs.Song
import com.jadebyte.jadeplayer.main.songs.SongsViewModel
import kotlinx.android.synthetic.main.fragment_playback.*
import java.util.concurrent.TimeUnit


class PlaybackFragment : BaseFragment(), View.OnClickListener {

    private lateinit var viewModel: SongsViewModel // Use SongsViewModel. Change later
    private lateinit var currentSong: Song
    private var items: List<Song>? = null
    private lateinit var rotationAnimSet: AnimatorSet

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
        updateViewsAsPerSongChange(viewPager.currentItem)
    }

    private fun setupViewViews() {
        rotationAnimSet = AnimatorInflater.loadAnimator(activity, R.animator.album_art_rotation) as AnimatorSet
        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                updateViewsAsPerSongChange(position)
            }

        })
        viewPager.setPageTransformer(true, ZoomOutPageTransformer())
        viewPager.adapter = PlaybackAdapter(items)
        songTitle.setFactory { LayoutInflater.from(activity).inflate(R.layout.song_title, null) as TextView }
        songArtist.setFactory { LayoutInflater.from(activity).inflate(R.layout.song_artist, null) as TextView }
        sectionBackButton.setOnClickListener(this)
        nextButton.setOnClickListener(this)
        previousButton.setOnClickListener(this)
        playButton.setOnClickListener(this)
        lyricsButton.setOnClickListener(this)
        closeButton.setOnClickListener(this)
    }

    private fun updateViewsAsPerSongChange(position: Int) {
        items?.get(position)?.let {
            rotationAnimSet.setTarget(viewPager.findViewWithTag(it))
            songArtist.setText(it.album.artist)
            songTitle.setText(it.title)
            rotationAnimSet.start()
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
        }
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
        val lyricsIsEmpty = TextUtils.isEmpty(lyrics)
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

    private fun hasLyrics(): Boolean {
        return !TextUtils.isEmpty(lyricsText.text)
    }

    override fun onDestroyView() {
        rotationAnimSet.cancel()
        super.onDestroyView()
    }


}

const val slideDuration = 600L
val translationY = 110F.px
