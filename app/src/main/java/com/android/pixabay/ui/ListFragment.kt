package com.android.pixabay.ui


import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.android.pixabay.R
import com.android.pixabay.State
import com.android.pixabay.model.SharedViewModel
import com.android.pixabay.ui.adapter.ImageAdapter

class ListFragment : Fragment() {
    var searchQuery: String? = null
    lateinit var imageRecyclerView : RecyclerView
    lateinit var errorTextView : TextView
    lateinit var progressBar : ProgressBar
    private lateinit var viewModel: SharedViewModel
    private lateinit var imagesListAdapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = activity?.run {
            ViewModelProviders.of(this).get(SharedViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        setHasOptionsMenu(true)
        imageRecyclerView = view.findViewById(R.id.image) as RecyclerView
        progressBar = view.findViewById(R.id.progress_bar) as ProgressBar
        errorTextView = view.findViewById(R.id.txt_error) as TextView

        initAdapter()
        initState()
        //  setUi()
  /*   adapter.setOnItemClickListener(object : OldImageAdapter.ClickListener{
            override fun onItemClick(v: View, position: Int) {
                model.setSelectedImage(adapter.getHitAtPosition(position)!!)
                v.findNavController().navigate(R.id.action_listFragment_to_detailFragment)
            }
        })*/
       /* //checkConnection()
        if (savedInstanceState != null) {
            searchQuery = savedInstanceState.getString(SEARCH_QUERY)
        } else {
            model.loadImages(getString(R.string.fruits))
        }
        model.images.observe(this, Observer<List<Hit>> { images ->
            if (images.isNotEmpty()){
                adapter.setImageData(images)
                adapter.notifyDataSetChanged()
                if (error_empty_view.isShown()){
                    error_empty_view.visibility = View.GONE
                    imageRecyclerView.visibility = View.VISIBLE
                }
            } else {
                val noResults: String = getString(R.string.no_results, searchQuery)
                error_empty_view.text = noResults
                error_empty_view.visibility = View.VISIBLE
                imageRecyclerView.visibility = View.GONE
            }
        })*/
        return view
    }
    /*fun setUi(){
        /*  retry_connection_check_button.setOnClickListener {
              if (ConnectionController.isInternetAvailable(this@MainActivity)!!){
                  val intent = getIntent()
                  intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                  finish()
                  startActivity(intent)
                  overridePendingTransition(0,0)
              }
          }*/
    }*/

   /* private fun checkConnection(){
        if (!ConnectionController.isInternetAvailable(activity)!!){
            imageRecyclerView.visibility = View.GONE
            error_empty_view.text = getString(R.string.no_connection)
            error_empty_view.visibility = View.VISIBLE
            retry_connection_check_button.visibility = View.VISIBLE
        }
    }*/

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
                        searchQuery = query
                       // model.loadImages(searchQuery!!)
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
        viewModel.imagesList.observe(this, Observer {
            imagesListAdapter.submitList(it)
        })

        imagesListAdapter.setOnItemClickListener(object: ImageAdapter.OnItemClickListener{
            override fun onItemClick(v: View, pos: Int) {
                viewModel.setSelectedImage(imagesListAdapter.getHitAtPosition(pos)!!)
                view?.findNavController()?.navigate(R.id.action_listFragment_to_detailFragment)
            }
        })
    }

    private fun initState() {
        errorTextView.setOnClickListener { viewModel.retry() }
        viewModel.getState().observe(this, Observer { state ->
            progressBar.visibility = if (viewModel.listIsEmpty() && state == State.LOADING) View.VISIBLE else View.GONE
            errorTextView.visibility = if (viewModel.listIsEmpty() && state == State.ERROR) View.VISIBLE else View.GONE
            if (!viewModel.listIsEmpty()) {
                imagesListAdapter.setState(state ?: State.DONE)
            }
        })
    }
}