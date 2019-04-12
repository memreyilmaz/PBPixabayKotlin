package com.payback.pbpixabaykotlin.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.payback.pbpixabaykotlin.ConnectionController
import com.payback.pbpixabaykotlin.R
import com.payback.pbpixabaykotlin.SEARCH_QUERY
import com.payback.pbpixabaykotlin.SELECTED_IMAGE
import com.payback.pbpixabaykotlin.adapter.ImageAdapter
import com.payback.pbpixabaykotlin.model.Hit
import com.payback.pbpixabaykotlin.model.ImageViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var searchQuery: String? = null
    lateinit var adapter : ImageAdapter
    lateinit var imageRecyclerView : RecyclerView
   // lateinit var binding: com.payback.pbpixabaykotlin.databinding.ActivityMainBinding
    val imageViewModel : ImageViewModel by lazy {
        ViewModelProviders.of(this).get(ImageViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


       setUi()
        setRecyclerView()
        checkConnection()
        if (savedInstanceState != null) {
            searchQuery = savedInstanceState.getString(SEARCH_QUERY)
        } else {
            imageViewModel.loadImages(getString(R.string.fruits))
        }
        imageViewModel.images.observe(this, Observer<List<Hit>> {images ->
            if (images.isNotEmpty()){
                adapter.setImageData(images)
                adapter.notifyDataSetChanged()
                if (error_empty_view.isShown()){
                //    if (binding.errorEmptyView.isShown()){
                    error_empty_view.visibility = View.GONE
                    //binding.errorEmptyView.visibility = View.GONE
                    imageRecyclerView.visibility = View.VISIBLE
                    //binding.image.visibility = View.VISIBLE
                }
            } else {
                val noResults: String = getString(R.string.no_results, searchQuery)
                error_empty_view.text = noResults
                //binding.errorEmptyView.text = noResults
                error_empty_view.visibility = View.VISIBLE
                //binding.errorEmptyView.visibility = View.VISIBLE
                imageRecyclerView.visibility = View.GONE
                //binding.image.visibility = View.GONE
            }
        })
    }
    fun setUi(){
        setSupportActionBar(main_activity_toolbar)

        retry_connection_check_button.setOnClickListener {
            if (ConnectionController.isInternetAvailable(this@MainActivity)!!){
                val intent = getIntent()
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                finish()
                startActivity(intent)
                overridePendingTransition(0,0)
            }
        }
    }

    fun setRecyclerView(){
        imageRecyclerView = findViewById(R.id.image) as RecyclerView
        imageRecyclerView.setHasFixedSize(true)
        //binding.image.setHasFixedSize(true)
        adapter = ImageAdapter()
        imageRecyclerView.adapter = adapter
        //binding.image.adapter = adapter
        adapter.setOnItemClickListener(object : ImageAdapter.ClickListener{
            override fun onItemClick(v: View, position: Int) {
                showDetailActivity(position)
            }
        })
    }

    private fun checkConnection(){
       if (!ConnectionController.isInternetAvailable(this@MainActivity)!!){
           imageRecyclerView.visibility = View.GONE
           //binding.image.visibility = View.GONE
           error_empty_view.text = getString(R.string.no_connection)
           //binding.errorEmptyView.text = getString(R.string.no_connection)
           error_empty_view.visibility = View.VISIBLE
           //binding.errorEmptyView.visibility = View.VISIBLE
           retry_connection_check_button.visibility = View.VISIBLE
           //binding.retryConnectionCheckButton.visibility = View.VISIBLE
        }
    }
    fun showDetailActivity(position: Int){
           val detailIntent = Intent(this, DetailActivity::class.java)
           val selectedImage: Hit? = adapter.getHitAtPosition(position)
           detailIntent.putExtra(SELECTED_IMAGE, selectedImage)
           startActivity(detailIntent)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main_action, menu)
        val searchItem = menu.findItem(R.id.action_bar_search_icon) as MenuItem
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.searchbar_hint)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                searchItem.collapseActionView()
                searchQuery = query
                imageViewModel.loadImages(searchQuery!!)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        return true
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY, searchQuery)
    }
}