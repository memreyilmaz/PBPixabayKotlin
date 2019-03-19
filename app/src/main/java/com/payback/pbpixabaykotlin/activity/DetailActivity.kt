package com.payback.pbpixabaykotlin.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.payback.pbpixabaykotlin.R
import com.payback.pbpixabaykotlin.SELECTED_IMAGE
import com.payback.pbpixabaykotlin.model.Hit
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail.*
import java.lang.String.valueOf

class DetailActivity : AppCompatActivity() {
    lateinit var hit: Hit
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val detailIntent = intent
        hit = detailIntent.getParcelableExtra(SELECTED_IMAGE)
        setTitle(hit.user)
        setUi()
    }

    private fun setUi(){
        setSupportActionBar(detail_activity_toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);

        Picasso.with(this)
            .load(hit.largeImageURL)
            .placeholder(R.drawable.pixabay)
            .error(R.drawable.pixabay)
            .into(detail_imageView);

        image_tags_textView.text = hit.tags.replace(","," /")
        image_likes_textView.text = valueOf(hit.likes)
        image_favourites_textView.text = valueOf(hit.favorites)
        image_comments_textView.text = valueOf(hit.comments)
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
        }
        return super.onOptionsItemSelected(item)
    }
    private fun shareCurrentImage() {
        val shareStringBuilder = StringBuilder()
        shareStringBuilder.append(getString(R.string.share_image_headline))
                            .append("\n")
                            .append(hit.pageURL)

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareStringBuilder.toString())
        shareIntent.type = "text/plain"
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_with)))
    }
}