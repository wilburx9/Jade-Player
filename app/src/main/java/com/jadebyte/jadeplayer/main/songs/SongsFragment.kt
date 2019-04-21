// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.songs


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.jadebyte.jadeplayer.R
import kotlinx.android.synthetic.main.fragment_explore.navigationIcon
import kotlinx.android.synthetic.main.fragment_songs.*

class SongsFragment : Fragment(), View.OnClickListener {

    private lateinit var adapter: SongsAdapter
    private var items: List<Song> = emptyList()
    private lateinit var viewModel: SongsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_songs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this)[SongsViewModel::class.java]
        setupRecyclerView()
        observeViewModel()
        navigationIcon.setOnClickListener(
            Navigation.createNavigateOnClickListener(
                R.id.action_songsFragment_to_navigationDialogFragment
            )
        )
        play.setOnClickListener(this)
    }

    private fun observeViewModel() {
        viewModel.data.observe(viewLifecycleOwner, Observer(::updateViews))
    }

    private fun updateViews(items: List<Song>) {
        this.items = items
        adapter.updateItems(items)
        tracks.text = resources.getQuantityString(R.plurals.numberOfTracks, items.count(), items.count())
    }

    private fun setupRecyclerView() {
        adapter = SongsAdapter(items)
        songsRV.adapter = adapter
        songsRV.layoutManager = LinearLayoutManager(activity)
    }

    override fun onClick(v: View?) {
        when(v?.id) {

        }
    }
}
