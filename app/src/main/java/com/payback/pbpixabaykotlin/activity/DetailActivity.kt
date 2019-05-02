package com.payback.pbpixabaykotlin.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.payback.pbpixabaykotlin.R
import com.payback.pbpixabaykotlin.SELECTED_IMAGE
import com.payback.pbpixabaykotlin.databinding.ActivityDetailBinding
import com.payback.pbpixabaykotlin.model.Hit
import com.payback.pbpixabaykotlin.model.ImageViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailBinding
    lateinit var hit: Hit
    val imageViewModel : ImageViewModel by lazy {
        ViewModelProviders.of(this).get(ImageViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        val detailIntent = intent
        hit = detailIntent.getParcelableExtra(SELECTED_IMAGE)
        binding.hit = hit
        setTitle(hit.user)
        setUi()

        binding.detailImageView.setOnViewTapListener { view, x, y ->
            when (detail_activity_toolbar.visibility) {
                View.VISIBLE -> setDetailsInvisible()
                View.GONE -> setDetailsVisible()}
        }
    }

    private fun setUi(){
        setSupportActionBar(detail_activity_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
      menuInflater.inflate(R.menu.activity_detail_action, menu)
      return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_bar_share_icon -> {
                shareCurrentImage()
                return true
            }
            R.id.action_bar_download_icon ->{
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ), 1
                    )
                }
                val url = hit.largeImageURL
                val imageName = hit.id.toString()
                imageViewModel.downloadImage(detail_imageView,imageName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy (
                        onSuccess = { file ->
                            Toast.makeText(this, "$file saved", Toast.LENGTH_SHORT).show()
                        },
                        onError = { e ->
                            Toast.makeText(this, "Error saving file :${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }                    )
                /*imageViewModel.saveImage(url, imageName)
                imageViewModel.downloadstatus.observe(this, Observer {
                    when(it){
                       true -> Snackbar.make(detail_layout, R.string.image_downloaded, Snackbar.LENGTH_LONG).show()
                       false -> Snackbar.make(detail_layout, R.string.image_not_downloaded, Snackbar.LENGTH_LONG).show()
                    }
                })*/
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun shareCurrentImage() {
        val shareStringBuilder = StringBuilder()
        shareStringBuilder.append(getString(com.payback.pbpixabaykotlin.R.string.share_image_headline))
                            .append("\n")
                            .append(hit.pageURL)

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareStringBuilder.toString())
        shareIntent.type = "text/plain"
        startActivity(Intent.createChooser(shareIntent, getString(com.payback.pbpixabaykotlin.R.string.share_with)))
    }

    private fun setDetailsVisible(){
        binding.detailActivityToolbar.visibility = View.VISIBLE
        binding.likesIcon.visibility = View.VISIBLE
        binding.imageLikesTextView.visibility = View.VISIBLE
        binding.favouritesIcon.visibility = View.VISIBLE
        binding.imageFavouritesTextView.visibility = View.VISIBLE
        binding.commentsIcon.visibility = View.VISIBLE
        binding.imageCommentsTextView.visibility = View.VISIBLE
        binding.imageTagsTextView.visibility = View.VISIBLE
    }

    private fun setDetailsInvisible(){
        binding.detailActivityToolbar.visibility = View.GONE
        binding.likesIcon.visibility = View.GONE
        binding.imageLikesTextView.visibility = View.GONE
        binding.favouritesIcon.visibility = View.GONE
        binding.imageFavouritesTextView.visibility = View.GONE
        binding.commentsIcon.visibility = View.GONE
        binding.imageCommentsTextView.visibility = View.GONE
        binding.imageTagsTextView.visibility = View.GONE
    }
}