package com.payback.pbpixabaykotlin.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.payback.pbpixabaykotlin.R
import com.payback.pbpixabaykotlin.SEARCH_QUERY
import com.payback.pbpixabaykotlin.adapter.ImageAdapter
import com.payback.pbpixabaykotlin.model.Hit
import com.payback.pbpixabaykotlin.model.ImageViewModel

class MainActivity : AppCompatActivity() {

    //AlertDialog.Builder builder;
    //ImageViewModel imageViewModel;

    lateinit var retryButton: Button
    lateinit var emptyView : TextView

    lateinit var searchQuery: String
    var images : List<Hit>? = null
    lateinit var adapter : ImageAdapter
    lateinit var imageRecyclerView : RecyclerView
    val imageViewModel : ImageViewModel by lazy {
        ViewModelProviders.of(this).get(ImageViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        emptyView = findViewById(R.id.user_name_textView)
//        retryButton = findViewById(R.id.retry_connection_check_button)

        val toolbar = findViewById(R.id.main_activity_toolbar) as Toolbar
        setSupportActionBar(toolbar)
        imageRecyclerView = findViewById(R.id.image) as RecyclerView
        adapter = ImageAdapter()
        imageRecyclerView.adapter = adapter
        searchQuery = "fruits"
        imageViewModel.loadImages(searchQuery)

        imageViewModel.getImages().observe(this, Observer<List<Hit>> {images ->
            adapter.setImageData(images);
            adapter.notifyDataSetChanged();
        })
    }
    fun showDetailActivity(position: Int){}
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_action, menu)
        val searchItem = menu!!.findItem(R.id.action_bar_search_icon) as MenuItem
        val searchView = searchItem.actionView as SearchView
        searchView.setQueryHint(resources.getString(R.string.searchbar_hint))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                                searchItem.collapseActionView()
                searchQuery = query
                imageViewModel.loadImages(searchQuery)
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
        outState.putString(SEARCH_QUERY,searchQuery)
    }
}
