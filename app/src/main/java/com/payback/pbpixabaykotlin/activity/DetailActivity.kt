package com.payback.pbpixabaykotlin.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.github.chrisbanes.photoview.OnViewTapListener
import com.payback.pbpixabaykotlin.SELECTED_IMAGE
import com.payback.pbpixabaykotlin.databinding.ActivityDetailBinding
import com.payback.pbpixabaykotlin.model.Hit
import kotlinx.android.synthetic.main.activity_detail.*


class DetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailBinding
    lateinit var hit: Hit
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, com.payback.pbpixabaykotlin.R.layout.activity_detail)
        val detailIntent = intent
        hit = detailIntent.getParcelableExtra(SELECTED_IMAGE)
        binding.hit = hit
        setTitle(hit.user)
        setUi()

        binding.detailImageView.setOnViewTapListener(object: OnViewTapListener{
            override fun onViewTap(view: View?, x: Float, y: Float) {
                when (detail_activity_toolbar.visibility) {
                    View.VISIBLE -> setDetailsInvisible()
                    View.GONE -> setDetailsVisible()}
            }
        })
    }

    private fun setUi(){
        setSupportActionBar(detail_activity_toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
      menuInflater.inflate(com.payback.pbpixabaykotlin.R.menu.activity_detail_action, menu)
      return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            com.payback.pbpixabaykotlin.R.id.action_bar_share_icon -> {
                shareCurrentImage()
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