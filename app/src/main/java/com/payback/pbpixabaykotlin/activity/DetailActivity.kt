package com.payback.pbpixabaykotlin.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.payback.pbpixabaykotlin.R
import com.payback.pbpixabaykotlin.SELECTED_IMAGE
import com.payback.pbpixabaykotlin.model.Hit
import com.squareup.picasso.Picasso
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
        val bigImage: ImageView = findViewById(R.id.detail_imageView)
        val imageTags: TextView = findViewById(R.id.image_tags_textView)
        val imageLikesCount: TextView = findViewById(R.id.image_likes_textView)
        val imageFavouritesCount: TextView = findViewById(R.id.image_favourites_textView)
        val imageCommentsCount: TextView = findViewById(R.id.image_comments_textView)
        val toolbar = findViewById(R.id.detail_activity_toolbar) as Toolbar
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);

        Picasso.with(this)
            .load(hit.largeImageURL)
            .placeholder(R.drawable.pixabay)
            .error(R.drawable.pixabay)
            .into(bigImage);

        imageTags.text = hit.tags.replace(","," /")
        imageLikesCount.text = valueOf(hit.likes)
        imageFavouritesCount.text = valueOf(hit.favorites)
        imageCommentsCount.text = valueOf(hit.comments)
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