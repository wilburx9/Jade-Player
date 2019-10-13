// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.genres


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jadebyte.jadeplayer.BR
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.callbacks.OnItemClickListener
import com.jadebyte.jadeplayer.main.common.view.BaseAdapter
import com.jadebyte.jadeplayer.main.common.view.BaseFragment
import com.reddit.indicatorfastscroll.FastScrollItemIndicator
import kotlinx.android.synthetic.main.fragment_genres.*
import java.util.*


class GenresFragment : BaseFragment(), OnItemClickListener {

    private var items: List<Genre> = emptyList()
    private lateinit var viewModel: GenresViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this)[GenresViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_genres, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    @Suppress("UNCHECKED_CAST")
    private fun observeViewModel() {
        viewModel.init()
        viewModel.items.observe(viewLifecycleOwner, Observer {
            this.items = it
            (genresRV.adapter as BaseAdapter<Genre>).updateItems(this.items)
        })
    }

    private fun setupViews() {
        genresRV.adapter =
            BaseAdapter(items, activity!!, R.layout.item_genre, BR.genre, this, longClick = true)
        genresRV.layoutManager = LinearLayoutManager(activity)
        navigationIcon.setOnClickListener {
            it.findNavController().navigate(R.id.action_genresFragment_to_navigationDialogFragment)
        }
        fastScroller.setupWithRecyclerView(genresRV, {
            FastScrollItemIndicator.Text(items[it].name.substring(0, 1).toUpperCase(Locale.getDefault()))
        })
        fastScrollerThumb.setupWithFastScroller(fastScroller)
    }


    override fun onItemClick(position: Int, sharableView: View?) {
        val transitionName = ViewCompat.getTransitionName(sharableView!!)!!
        val extras = FragmentNavigator.Extras.Builder()
            .addSharedElement(sharableView, transitionName)
            .build()
        val action = GenresFragmentDirections.actionGenresFragmentToGenreSongsFragment(items[position], transitionName)
        findNavController().navigate(action, extras)
    }

    override fun onItemLongClick(position: Int) {
        super.onItemLongClick(position)
        val action = GenresFragmentDirections.actionGenresFragmentToGenresMenuBottomSheetDialogFragment(
            genre =
            items[position]
        )
        findNavController().navigate(action)

    }

}
