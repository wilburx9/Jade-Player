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
    var items = emptyList<T>()
    lateinit var viewModel: BaseViewModel<T>
    @get: IdRes abstract var navigationFragmentId: Int
    @get: PluralsRes open var numberOfDataRes: Int = -1
    @get: StringRes open var titleRes: Int = -1
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


    @Suppress("UNCHECKED_CAST")
    private fun updateViews(items: List<T>) {
        this.items = items
        (dataRV.adapter as BaseAdapter<T>).updateItems(items)
        dataNum.text = resources.getQuantityString(numberOfDataRes, items.count(), items.count())
    }

    private fun setupView() {
        if (titleRes > -1) {
            sectionTitle.setText(titleRes)
        }
        val adapter =
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

    abstract override fun onItemClick(position: Int, art: ImageView?)

}