// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.callbacks.OnItemClickListener
import com.jadebyte.jadeplayer.main.common.data.BaseViewModel
import kotlinx.android.synthetic.main.fragment_base_player.*
import kotlinx.android.synthetic.main.fragment_explore.navigationIcon

/**
 * Created by Wilberforce on 2019-04-21 at 01:48.
 */
abstract class BasePlayerFragment<T> : BaseFragment(), View.OnClickListener, OnItemClickListener {
    private var adapter: BaseAdapter<T>? = null
    var items = emptyList<T>()
    lateinit var viewModel: BaseViewModel<T>
    @get: IdRes abstract var navigationFragmentId: Int
    @get: PluralsRes abstract var numberOfDataRes: Int
    @get: StringRes abstract var titleRes: Int
    @get: LayoutRes abstract var itemLayoutId: Int
    abstract var viewModelVariableId: Int
    open var fadeInViewHolder = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_base_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeViewModel()
        navigationIcon.setOnClickListener(
            Navigation.createNavigateOnClickListener(
                navigationFragmentId
            )
        )
        playButton.setOnClickListener(this)
    }

    private fun observeViewModel() {
       if (items.isEmpty()) {
           viewModel.init()
           viewModel.data.observe(viewLifecycleOwner, Observer(::updateViews))
       } else {
           viewModel.data.value = items
       }
    }

    private fun updateViews(items: List<T>) {
        this.items = items
        adapter?.updateItems(items)
        dataNum.text = resources.getQuantityString(numberOfDataRes, items.count(), items.count())
    }

    private fun setupView() {
        title.setText(titleRes)
        adapter =
            BaseAdapter(items, activity!!, itemLayoutId, viewModelVariableId, fadeInViewHolder, this)
        dataRV.adapter = adapter
        dataRV.layoutManager = layoutManager()
    }

    open fun layoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(activity)
    }

    override fun onClick(v: View?) {
        when (v?.id) {

        }
    }

    abstract override fun onItemClick(position: Int, albumArt: ImageView?)


    // Detach the adapter from the RecyclerView. Was causing memory leaks.
    // I am not doing this in onDestroy because it was messing-up the life cycle of the viewModel

    // More info: https://stackoverflow.com/a/46957469/6181476 and https://stackoverflow.com/q/54581071/6181476
    override fun onDestroy() {
        dataRV?.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(v: View?) {
                dataRV?.adapter = null
                adapter = null
            }


            override fun onViewAttachedToWindow(v: View?) {}

        })
        super.onDestroy()
    }
}