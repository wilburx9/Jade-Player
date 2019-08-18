// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.genres


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.jadebyte.jadeplayer.BR
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.databinding.FragmentGenreSongsBinding
import com.jadebyte.jadeplayer.main.common.callbacks.OnItemClickListener
import com.jadebyte.jadeplayer.main.common.view.BaseAdapter
import com.jadebyte.jadeplayer.main.common.view.BaseFragment
import com.jadebyte.jadeplayer.main.songs.Song
import kotlinx.android.synthetic.main.fragment_genre_songs.*


class GenreSongsFragment : BaseFragment(), OnItemClickListener, View.OnClickListener {
    private lateinit var viewModel: GenreSongsViewModel
    private lateinit var genre: Genre
    private var items = emptyList<Song>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        genre = arguments!!.getParcelable("genre")!!
        viewModel = ViewModelProviders.of(this)[GenreSongsViewModel::class.java]
        viewModel.init(genre.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentGenreSongsBinding.inflate(inflater, container, false)
        binding.genre = genre
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setTransitionName(genreNameField, arguments!!.getString("transitionName"))
        setupViews()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.items.observe(viewLifecycleOwner, Observer(this::updateViews))
    }

    @Suppress("UNCHECKED_CAST")
    private fun updateViews(items: List<Song>) {
        if (items.isEmpty()) {
            findNavController().popBackStack()
            return
        }
        this.items = items
        genreSongsDuration.text = getSongsTotalTime(items)
        (genreSongsRV.adapter as BaseAdapter<Song>).updateItems(items)
    }

    private fun setupViews() {
        val adapter = BaseAdapter(items, activity!!, R.layout.item_model_song, BR.song, itemClickListener = this)
        genreSongsRV.adapter = adapter
        genreSongsRV.layoutManager = LinearLayoutManager(activity!!)
        sectionBackButton.setOnClickListener(this)
        moreOptions.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.sectionBackButton -> findNavController().popBackStack()
            R.id.moreOptions -> findNavController().navigate(
                GenreSongsFragmentDirections
                    .actionGenreSongsFragmentToGenresMenuBottomSheetDialogFragment(genre = genre)
            )
        }
    }

    override fun onItemClick(position: Int, sharableView: View?) {

    }
}
