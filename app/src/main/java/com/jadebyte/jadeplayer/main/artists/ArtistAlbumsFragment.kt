// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.artists


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.jadebyte.jadeplayer.BR
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.common.px
import com.jadebyte.jadeplayer.databinding.FragmentArtistAlbumsBinding
import com.jadebyte.jadeplayer.main.albums.Album
import com.jadebyte.jadeplayer.main.common.callbacks.OnItemClickListener
import com.jadebyte.jadeplayer.main.common.view.BaseAdapter
import kotlinx.android.synthetic.main.fragment_artist_albums.*


private const val ARTIST = "artist"

class ArtistAlbumsFragment : Fragment(), OnItemClickListener {
    lateinit var artist: Artist
    lateinit var viewModel: ArtistAlbumsViewModel
    private var items = emptyList<Album>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        artist = arguments!!.getParcelable(ARTIST)!!
        viewModel = ViewModelProviders.of(this)[ArtistAlbumsViewModel::class.java]
        viewModel.init(artist.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentArtistAlbumsBinding>(
            inflater,
            R.layout.fragment_artist_albums,
            container,
            false
        )
        val view = binding.root
        binding.artist = artist
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setTransitionName(sectionTitle, arguments!!.getString("transitionName"))
        setupRecyclerView()
        observeViewModel()
        sectionBackButton.setOnClickListener { findNavController().popBackStack() }
        navigationIcon.setOnClickListener(
            Navigation.createNavigateOnClickListener(
                R.id.action_artistAlbumsFragment_to_navigationDialogFragment
            )
        )
    }

    private fun observeViewModel() {
        viewModel.data.observe(viewLifecycleOwner, Observer {
            updateViews(it)
        })
    }

    @Suppress("UNCHECKED_CAST")
    private fun updateViews(items: List<Album>) {
        this.items = items
        (artistAlbumsRV.adapter as BaseAdapter<Album>).updateItems(items)
    }

    @SuppressLint("WrongConstant")
    private fun setupRecyclerView() {
        val adapter = BaseAdapter(items, activity!!, R.layout.item_album, BR.album, itemClickListener = this)
        if(artist.albumsCount ==1) {
            artistAlbumsRV.setPadding(20.px, 0,0, 0)
        }
        artistAlbumsRV.adapter = adapter
        artistAlbumsRV.layoutManager = FlexboxLayoutManager(activity).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = if (artist.albumsCount > 1) JustifyContent.SPACE_EVENLY else JustifyContent.FLEX_START
        }


    }

    override fun onItemClick(position: Int, sharableView: View?) {
        val transitionName = ViewCompat.getTransitionName(sharableView!!)!!
        val extras = FragmentNavigator.Extras.Builder()
            .addSharedElement(sharableView, transitionName)
            .build()
        val action =
            ArtistAlbumsFragmentDirections.actionArtistAlbumsFragmentToAlbumSongsFragment(
                items[position],
                transitionName
            )
        findNavController().navigate(action, extras)
    }

}
