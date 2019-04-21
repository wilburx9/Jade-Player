// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.explore


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.albums.Album
import kotlinx.android.synthetic.main.fragment_explore.*


class ExploreFragment : Fragment() {

    private lateinit var adapter: ExploreAdapter
    private var items: List<Album> = emptyList()
    private lateinit var viewModel: ExploreViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_explore, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this)[ExploreViewModel::class.java]
        setupRecyclerView()
        observeViewModel()
        navigationIcon.setOnClickListener(
            Navigation.createNavigateOnClickListener(
                R.id.action_exploreFragment_to_navigationDialogFragment
            )
        )

    }

    private fun observeViewModel() {
        viewModel.data.observe(viewLifecycleOwner, Observer {
            this.items = it
            adapter.updateItems(it)
        })
    }

    private fun setupRecyclerView() {
        adapter = ExploreAdapter(items)
        randomAlbumsRV.adapter = adapter

        val layoutManager = LinearLayoutManager(activity, LinearLayout.HORIZONTAL, false)
        randomAlbumsRV.layoutManager = layoutManager
    }


}
