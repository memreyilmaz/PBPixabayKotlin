package com.payback.pbpixabaykotlin.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
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
    val imageViewModel : ImageViewModel by lazy {
        ViewModelProviders.of(this).get(ImageViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUi()
        setRecyclerView()
        checkConnection()
        if (savedInstanceState != null) {
            searchQuery = savedInstanceState.getString(SEARCH_QUERY)
        } else {
            imageViewModel.loadImages(getString(R.string.fruits))
        }


        imageViewModel.getImages().observe(this, Observer<List<Hit>> {images ->
            if (images.size != 0){
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
        })
    }
    fun setUi(){
        setSupportActionBar(main_activity_toolbar)

        retry_connection_check_button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (ConnectionController.isInternetAvailable(this@MainActivity)){
                    val intent = getIntent()
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    finish()
                    startActivity(intent)
                    overridePendingTransition(0,0)
                }
            }
        })
    }

    fun setRecyclerView(){
        imageRecyclerView = findViewById(R.id.image) as RecyclerView
        imageRecyclerView.setHasFixedSize(true)
        adapter = ImageAdapter()
        imageRecyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : ImageAdapter.ClickListener{
            override fun onItemClick(v: View, position: Int) {
                showDetailActivity(position)
            }
        })
    }

    fun checkConnection(){
        if (!ConnectionController.isInternetAvailable(this@MainActivity)){
            imageRecyclerView.visibility = View.GONE
            error_empty_view.text = getString(R.string.no_connection)
            error_empty_view.visibility = View.VISIBLE
            retry_connection_check_button.visibility = View.VISIBLE
        }
    }
        fun showDetailActivity(position: Int){
        val alert = AlertDialog.Builder(this@MainActivity)
            alert.setMessage(R.string.dialog_message)
                .setCancelable(true)
                .setPositiveButton(R.string.dialog_yes) { dialog, which ->
                    val detailIntent = Intent(this, DetailActivity::class.java)
                    val selectedImage: Hit? = adapter.getHitAtPosition(position)
                    detailIntent.putExtra(SELECTED_IMAGE, selectedImage);
                    startActivity(detailIntent)
                }
                .setNegativeButton(R.string.dialog_no) {
                    dialog, which ->
                    dialog.cancel()
                }
            alert.show()
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main_action, menu)
        val searchItem = menu.findItem(R.id.action_bar_search_icon) as MenuItem
        val searchView = searchItem.actionView as SearchView
        searchView.setQueryHint(getString(R.string.searchbar_hint))

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