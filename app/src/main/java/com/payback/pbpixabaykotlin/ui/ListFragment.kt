package com.payback.pbpixabaykotlin.ui


import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.payback.pbpixabaykotlin.R
import com.payback.pbpixabaykotlin.SEARCH_QUERY
import com.payback.pbpixabaykotlin.adapter.ImageAdapter
import com.payback.pbpixabaykotlin.model.Hit
import com.payback.pbpixabaykotlin.model.SharedViewModel
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : Fragment() {
    var searchQuery: String? = null
    lateinit var adapter : ImageAdapter
    lateinit var imageRecyclerView : RecyclerView
    private lateinit var model: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        model = activity?.run {
            ViewModelProviders.of(this).get(SharedViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        setHasOptionsMenu(true)

        //  setUi()
        //setRecyclerView()
        imageRecyclerView = view.findViewById(R.id.image) as RecyclerView
        imageRecyclerView.setHasFixedSize(true)
        adapter = ImageAdapter()
        imageRecyclerView.adapter = adapter

        adapter.setOnItemClickListener(object : ImageAdapter.ClickListener{
            override fun onItemClick(v: View, position: Int) {
                model.setSelectedImage(adapter.getHitAtPosition(position)!!)
                v.findNavController().navigate(R.id.action_listFragment_to_detailFragment)
            }
        })
        //checkConnection()
        if (savedInstanceState != null) {
            searchQuery = savedInstanceState.getString(SEARCH_QUERY)
        } else {
            model.loadImages(getString(com.payback.pbpixabaykotlin.R.string.fruits))
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
                val noResults: String = getString(com.payback.pbpixabaykotlin.R.string.no_results, searchQuery)
                error_empty_view.text = noResults
                error_empty_view.visibility = View.VISIBLE
                imageRecyclerView.visibility = View.GONE
            }
        })

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

    /*fun setRecyclerView(){
            imageRecyclerView = view!!.findViewById(R.id.image) as RecyclerView
            imageRecyclerView.setHasFixedSize(true)
            adapter = ImageAdapter()
            imageRecyclerView.adapter = adapter

        adapter.setOnItemClickListener(object : ImageAdapter.ClickListener{
            override fun onItemClick(v: View, position: Int) {
              //  showDetailActivity(position)
            }
        })
    }*/

   /* private fun checkConnection(){
        if (!ConnectionController.isInternetAvailable(activity)!!){
            imageRecyclerView.visibility = View.GONE
            //binding.image.visibility = View.GONE
            error_empty_view.text = getString(R.string.no_connection)
            //binding.errorEmptyView.text = getString(R.string.no_connection)
            error_empty_view.visibility = View.VISIBLE
            //binding.errorEmptyView.visibility = View.VISIBLE
            retry_connection_check_button.visibility = View.VISIBLE
            //binding.retryConnectionCheckButton.visibility = View.VISIBLE
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
                searchView.queryHint = getString(com.payback.pbpixabaykotlin.R.string.searchbar_hint)

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                    override fun onQueryTextSubmit(query: String): Boolean {
                        searchItem.collapseActionView()
                        searchQuery = query
                        model.loadImages(searchQuery!!)
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
}