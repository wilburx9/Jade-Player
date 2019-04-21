// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.explore


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jadebyte.jadeplayer.BR
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.albums.Album
import com.jadebyte.jadeplayer.main.common.callbacks.OnItemClickListener
import com.jadebyte.jadeplayer.main.common.view.BaseAdapter
import kotlinx.android.synthetic.main.fragment_explore.*


class ExploreFragment : Fragment(), OnItemClickListener {

    private var adapter: BaseAdapter<Album>? = null
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
        viewModel.init()
        setupRecyclerView()
        observeViewModel()
        navigationIcon.setOnClickListener(
            Navigation.createNavigateOnClickListener(
                R.id.action_exploreFragment_to_navigationDialogFragment
            )
        )

    }

    private fun observeViewModel() {
        if (items.isEmpty()) {
            viewModel.data.observe(viewLifecycleOwner, Observer {
                this.items = it
                adapter?.updateItems(it)
            })
        } else {
            viewModel.data.value = items
        }
    }

    private fun setupRecyclerView() {
        adapter = BaseAdapter(items, activity!!, R.layout.item_album, BR.album, true, this)
        randomAlbumsRV.adapter = adapter

        val layoutManager = LinearLayoutManager(activity, LinearLayout.HORIZONTAL, false)
        randomAlbumsRV.layoutManager = layoutManager
    }

    override fun onItemClick(position: Int, albumArt: ImageView?) {
        val transitionName = ViewCompat.getTransitionName(albumArt!!)!!
        val extras = FragmentNavigator.Extras.Builder()
            .addSharedElement(albumArt, transitionName)
            .build()
        val action =
            ExploreFragmentDirections.actionExploreFragmentToAlbumSongsFragment(items[position], transitionName)
        findNavController().navigate(action, extras)
    }


    override fun onDestroy() {
        // Detach the adapter from the RecyclerView. Was causing memory leaks.
        // I am not doing this in onDestroy because it was messing-up the life cycle of the viewModel

        // More info: https://stackoverflow.com/a/46957469/6181476 and https://stackoverflow.com/q/54581071/6181476
        randomAlbumsRV?.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(v: View?) {
                randomAlbumsRV?.adapter = null
                adapter = null
            }

            override fun onViewAttachedToWindow(v: View?) {}

        })
        super.onDestroy()
    }

}
