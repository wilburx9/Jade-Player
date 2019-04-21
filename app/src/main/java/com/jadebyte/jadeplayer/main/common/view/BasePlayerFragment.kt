// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.data.BaseViewModel
import kotlinx.android.synthetic.main.fragment_base_player.*
import kotlinx.android.synthetic.main.fragment_explore.navigationIcon

/**
 * Created by Wilberforce on 2019-04-21 at 01:48.
 */
abstract class BasePlayerFragment<T> : Fragment(), View.OnClickListener {
    private lateinit var adapter: BaseAdapter<T>
    private var items: List<T> = emptyList()
    lateinit var viewModel: BaseViewModel<T>
    @get: IdRes abstract var navigationFragmentId: Int
    @get: PluralsRes abstract var numberOfDataRes: Int
    @get: StringRes abstract var titleRes: Int
    @get: LayoutRes abstract var itemLayoutId: Int
    abstract var viewModelVariableId: Int


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
        play.setOnClickListener(this)
    }

    private fun observeViewModel() {
        viewModel.data.observe(viewLifecycleOwner, Observer(::updateViews))
    }

    private fun updateViews(items: List<T>) {
        this.items = items
        adapter.updateItems(items)
        dataNum.text = resources.getQuantityString(numberOfDataRes, items.count(), items.count())
    }

    private fun setupView() {
        title.setText(titleRes)
        adapter =
            BaseAdapter(items, activity!!, itemLayoutId, viewModelVariableId)
        dataRV.adapter = adapter
        dataRV.layoutManager = LinearLayoutManager(activity)
    }

    override fun onClick(v: View?) {
        when (v?.id) {

        }
    }
}