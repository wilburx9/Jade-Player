// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.navigation

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.dragSwipe.ItemTouchHelperAdapter
import com.jadebyte.jadeplayer.main.common.dragSwipe.OnStartDragListener
import com.jadebyte.jadeplayer.main.common.dragSwipe.SimpleItemTouchHelperCallback
import com.jadebyte.jadeplayer.main.common.utils.BlurKit
import kotlinx.android.synthetic.main.fragment_navigation_dialog.*
import kotlinx.coroutines.*
import java.util.*


class NavigationDialogFragment : DialogFragment(), OnStartDragListener, ItemTouchHelperAdapter {

    private var origin: Int? = null
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var adapter: NavAdapter
    private lateinit var viewModel: NavViewModel
    private var items: List<NavItem> = emptyList()
    private val job = Job()
    private val scope = CoroutineScope(job + Dispatchers.Main)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialogStyle)
        arguments?.let {
            origin = it.getInt("origin")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_navigation_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this)[NavViewModel::class.java]
        viewModel.init(origin)
        setupRecyclerView()
        observeViewModel()
        closeButton.setOnClickListener { findNavController().popBackStack() }
    }


    private fun observeViewModel() {
        viewModel.navItems?.observe(viewLifecycleOwner, Observer {
            this.items = it
            adapter.updateItems(it)
        })
    }

    private fun setupRecyclerView() {
        adapter = NavAdapter(items, this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter

        val layoutManager = GridLayoutManager(activity, 3)
        recyclerView.layoutManager = layoutManager

        val callback = SimpleItemTouchHelperCallback(this)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }


    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        // This is not the best place to do this.
        // Data modification should be done in the repository but I currently can't find a way to do this in the
        // repository and communicate back to this View that items has moved and the indexes of the moved items.

        // So what I am doing here is, swapping the items, notifying the adapter and notifying the repository
        // to save the new indexes in  SharePreferences
        Collections.swap(items, fromPosition, toPosition)
        adapter.updateItems(items, fromPosition, toPosition)
        viewModel.swap(items)
        return true
    }


    private fun setBlurredBackground() {
        scope.launch {
            val bitmap = withContext(Dispatchers.IO) {
                BitmapDrawable(resources, getBlurredBitmap())
            }
            rootView.background = bitmap
        }
    }

    private fun getBlurredBitmap(): Bitmap? {
        val viewGroup = activity?.findViewById<ViewGroup>(R.id.container)
        if (viewGroup != null) {
            if (viewGroup.width > 0 && viewGroup.height > 0) {
                return BlurKit.getInstance()
                    .fastBlur(viewGroup, 10, 0.3F)
            }
        }
        return null
    }


    override fun onStart() {
        super.onStart()
        dialog?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            it.window?.setLayout(width, height)
        }
        setBlurredBackground()
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

}
