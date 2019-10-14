// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.search


import android.animation.Animator
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.common.fadeInSlideInHorizontally
import com.jadebyte.jadeplayer.common.fadeOutSlideOutHorizontally
import com.jadebyte.jadeplayer.main.common.callbacks.TextWatcher
import com.jadebyte.jadeplayer.main.common.data.Model
import kotlinx.android.synthetic.main.fragment_search.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class SearchFragment : Fragment(), View.OnClickListener {
    private val viewModel: SearchViewModel by sharedViewModel()
    private val handler = Handler()
    private var ascendingSortOrder = true
    private val minCharacterLength = 2
    private val queryDelayInMills = 500L
    private var animator: Animator? = null
    private val results = mutableListOf(
        Result(R.string.songs, Type.Songs),
        Result(R.string.albums, Type.Albums),
        Result(R.string.artists, Type.Artists),
        Result(R.string.genres, Type.Genres),
        Result(R.string.playlist, Type.Playlists)
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.songsResults.observe(viewLifecycleOwner, Observer { updateResultState(Type.Songs, it) })

        viewModel.albumsResults.observe(viewLifecycleOwner, Observer { updateResultState(Type.Albums, it) })

        viewModel.artistsResults.observe(viewLifecycleOwner, Observer { updateResultState(Type.Artists, it) })

        viewModel.genresResults.observe(viewLifecycleOwner, Observer { updateResultState(Type.Genres, it) })

        viewModel.playlistResults.observe(viewLifecycleOwner, Observer { updateResultState(Type.Playlists, it) })

        viewModel.searchNavigation.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let {
                if (it.navigatorExtras == null) {
                    findNavController().navigate(it.directions)
                } else {
                    findNavController().navigate(it.directions, it.navigatorExtras)
                }
            }
        })
    }

    private fun setupViews() {
        // Show keyboard
        searchText.requestFocus()
        searchText.addTextChangedListener(searchTextWatcher)
        val service = ContextCompat.getSystemService(activity!!, InputMethodManager::class.java)
        service!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        // Setup viewpager
        resultsPager.adapter = SearchAdapter(childFragmentManager, activity, results)
        resultsTab.setupWithViewPager(resultsPager)
        resultsPager.isSaveFromParentEnabled = false

        // Click listeners
        sectionBackButton.setOnClickListener(this)
        filterButton.setOnClickListener(this)
    }

    private val searchTextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            super.onTextChanged(s, start, before, count)

            // Remove any pending schedule to execute search function
            handler.removeCallbacksAndMessages(null)

            if (s == null || s.length <= minCharacterLength) {
                // Remove all fragments
                results.forEach { it.hasResults = false }
                updateAdapter()
                if (filterButton.visibility == View.VISIBLE) filterButton.fadeOutSlideOutHorizontally().start()
                return
            }

            // Schedule execution of search to queryDelayInMills after query text changes
            handler.postDelayed({
                viewModel.query(s.toString().trim(), ascendingSortOrder)
            }, queryDelayInMills)
        }
    }

    private fun updateAdapter() = (resultsPager.adapter as SearchAdapter).updateResults(results)

    private fun updateResultState(t: Type, items: List<Model>) {
        results.first { it.type == t }.hasResults = items.isNotEmpty()
        updateAdapter()
        animator?.cancel()
        animator = if (results.withResult.isEmpty()) {
            if (filterButton.visibility == View.VISIBLE) filterButton.fadeOutSlideOutHorizontally(duration = 500) else null
        } else {
            if (filterButton.visibility != View.VISIBLE) filterButton.fadeInSlideInHorizontally(duration = 500) else null
        }
        animator?.start()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.sectionBackButton -> findNavController().popBackStack()
            R.id.filterButton -> toggleFilter()
        }
    }

    private fun toggleFilter() {
        ascendingSortOrder = !ascendingSortOrder
        viewModel.query(searchText.text.toString().trim(), ascendingSortOrder)
    }

    override fun onDestroyView() {
        animator?.cancel()
        handler.removeCallbacksAndMessages(null)
        searchText.removeTextChangedListener(searchTextWatcher)
        // Close keyboard
        val service = ContextCompat.getSystemService(activity!!, InputMethodManager::class.java)
        service?.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        super.onDestroyView()
    }


}
