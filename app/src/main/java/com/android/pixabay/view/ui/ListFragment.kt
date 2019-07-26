package com.android.pixabay.view.ui


import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.android.pixabay.R
import com.android.pixabay.viewmodel.ViewModelFactory
import com.android.pixabay.utils.DEFAULT_SEARCH_QUERY
import com.android.pixabay.utils.LAST_SEARCH_QUERY
import com.android.pixabay.view.adapter.ImageAdapter
import com.android.pixabay.viewmodel.SharedViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class ListFragment : DaggerFragment() {
    lateinit var imageRecyclerView : RecyclerView
    private lateinit var viewModel: SharedViewModel
    private lateinit var imagesListAdapter: ImageAdapter

    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
//        viewModel = activity?.run {
//            ViewModelProviders.of(this, Injection.provideViewModelFactory()).get(SharedViewModel::class.java)
//        } ?: throw Exception("Invalid Activity")
        viewModel = activity?.run {
            ViewModelProviders.of(this, viewModelFactory).get(SharedViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        setHasOptionsMenu(true)
        imageRecyclerView = view.findViewById(R.id.image) as RecyclerView

        val query = savedInstanceState?.getString(LAST_SEARCH_QUERY) ?: DEFAULT_SEARCH_QUERY
        viewModel.showSearchResults(query)
        initAdapter()

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_list_action, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.action_bar_search_icon ->{
                val searchItem = item
                val searchView = searchItem.actionView as SearchView
                searchView.queryHint = getString(R.string.searchbar_hint)
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        searchItem.collapseActionView()
                        viewModel.showSearchResults(query)
                        return true
                    }
                    override fun onQueryTextChange(newText: String): Boolean {
                        return false
                    }
                })
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initAdapter() {
        imagesListAdapter = ImageAdapter { viewModel.retry() }
        imageRecyclerView.adapter = imagesListAdapter

        viewModel.imageslist.observe(this, Observer {
            imagesListAdapter.submitList(it)
        })

        viewModel.networkState.observe(this, Observer {
            imagesListAdapter.setState(it)
        })

        imagesListAdapter.setOnItemClickListener(object: ImageAdapter.OnItemClickListener{
            override fun onItemClick(v: View, pos: Int) {
                viewModel.setSelectedImage(imagesListAdapter.getHitAtPosition(pos)!!)
                view?.findNavController()?.navigate(R.id.action_listFragment_to_detailFragment)
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LAST_SEARCH_QUERY, viewModel.lastSearchQueryValue())
    }
}