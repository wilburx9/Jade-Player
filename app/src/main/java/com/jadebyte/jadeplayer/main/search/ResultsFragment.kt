// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.search


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.jadebyte.jadeplayer.BR
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.albums.albumItemAnimSet
import com.jadebyte.jadeplayer.main.common.callbacks.OnItemClickListener
import com.jadebyte.jadeplayer.main.common.data.Model
import com.jadebyte.jadeplayer.main.common.view.BaseAdapter
import kotlinx.android.synthetic.main.fragment_results.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

private const val RESULT = "RESULT"


class ResultsFragment<T : Model> : Fragment(), OnItemClickListener {

    lateinit var result: Result
    var items = emptyList<Model>()
    private val viewModel: SearchViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        result = arguments!!.getParcelable(RESULT)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    private fun observeViewModel() {
        when (result.type) {
            Type.Songs -> viewModel.songsResults.observe(viewLifecycleOwner, Observer {
                items = it
                updateData()
            })
            Type.Albums -> viewModel.albumsResults.observe(viewLifecycleOwner, Observer {
                items = it
                updateData()
            })
            Type.Artists -> viewModel.artistsResults.observe(viewLifecycleOwner, Observer {
                items = it
                updateData()
            })
            Type.Genres -> viewModel.genresResults.observe(viewLifecycleOwner, Observer {
                items = it
                updateData()
            })
            Type.Playlists -> viewModel.playlistResults.observe(viewLifecycleOwner, Observer {
                items = it
                updateData()
            })
        }
    }

    private fun setupViews() {
        val adapter = when (result.type) {
            Type.Songs -> BaseAdapter(items, activity!!, R.layout.item_song, BR.song, this)
            Type.Albums -> BaseAdapter(items, activity!!, R.layout.item_album, BR.album, this, albumItemAnimSet, true)
            Type.Artists -> BaseAdapter(items, activity!!, R.layout.item_artist, BR.artist, this)
            Type.Genres -> BaseAdapter(items, activity!!, R.layout.item_genre, BR.genre, this, longClick = true)
            Type.Playlists -> BaseAdapter(
                items, activity!!, R.layout.item_playlist, BR.playlist, this, longClick = true
            )
        }
        resultsRV.adapter = adapter
        resultsRV.layoutManager = LinearLayoutManager(activity)
    }

    @Suppress("UNCHECKED_CAST")
    private fun updateData() = (resultsRV.adapter as BaseAdapter<T>).updateItems(items as List<T>)

    override fun onItemClick(position: Int, sharableView: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemLongClick(position: Int) {
        super.onItemLongClick(position)
    }


    companion object {
        @JvmStatic fun <T : Model> newInstance(result: Result) =
            ResultsFragment<T>().apply {
                arguments = Bundle().apply {
                    putParcelable(RESULT, result)
                }
            }
    }
}
