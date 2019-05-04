package com.payback.pbpixabaykotlin.ui


import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.payback.pbpixabaykotlin.R
import com.payback.pbpixabaykotlin.databinding.FragmentDetailBinding
import com.payback.pbpixabaykotlin.model.Hit
import com.payback.pbpixabaykotlin.model.SharedViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_detail.*

class DetailFragment : Fragment() {
    lateinit var binding: FragmentDetailBinding
    lateinit var bhit: Hit
    private lateinit var model: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = activity?.run {
            ViewModelProviders.of(this).get(SharedViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        model.selectedImage.observe(this, Observer<Hit> {
            //if (it == null) {
            bhit = it
            (activity as AppCompatActivity).supportActionBar?.title = bhit.user


            binding.hit = bhit

           // }
        })
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_detail, container, false)
        // binding.hit = bhit
        setHasOptionsMenu(true)

        binding.detailImageView.setOnViewTapListener { view, x, y ->
            when (likes_icon.visibility) {
                View.VISIBLE -> setDetailsInvisible()
                View.GONE -> setDetailsVisible()}
        }
        return binding.root
    }

    private fun shareCurrentImage() {
        val shareStringBuilder = StringBuilder()
        shareStringBuilder.append(getString(R.string.share_image_headline))
            .append("\n")
            .append(bhit.pageURL)

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareStringBuilder.toString())
        shareIntent.type = "text/plain"
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_with)))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_detail_action, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_bar_share_icon -> {
                shareCurrentImage()
                return true
            }
            R.id.action_bar_download_icon ->{
                /*if (ContextCompat.checkSelfPermission(context(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ), 1
                    )
                }*/
              //  val url = bhit.largeImageURL
                val imageName = bhit.id.toString()
                model.downloadImage(detail_imageView,imageName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy (
                        onSuccess = { file ->
                            Toast.makeText(context, "$file saved", Toast.LENGTH_SHORT).show()
                        },
                        onError = { e ->
                            Toast.makeText(context, "Error saving file :${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }                    )
                /*sharedViewModel.saveImage(url, imageName)
                sharedViewModel.downloadstatus.observe(this, Observer {
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

    private fun setDetailsVisible(){
        (activity as AppCompatActivity).supportActionBar?.show()
        binding.likesIcon.visibility = View.VISIBLE
        binding.imageLikesTextView.visibility = View.VISIBLE
        binding.favouritesIcon.visibility = View.VISIBLE
        binding.imageFavouritesTextView.visibility = View.VISIBLE
        binding.commentsIcon.visibility = View.VISIBLE
        binding.imageCommentsTextView.visibility = View.VISIBLE
        binding.imageTagsTextView.visibility = View.VISIBLE
    }

    private fun setDetailsInvisible(){
        (activity as AppCompatActivity).supportActionBar?.hide()
        binding.likesIcon.visibility = View.GONE
        binding.imageLikesTextView.visibility = View.GONE
        binding.favouritesIcon.visibility = View.GONE
        binding.imageFavouritesTextView.visibility = View.GONE
        binding.commentsIcon.visibility = View.GONE
        binding.imageCommentsTextView.visibility = View.GONE
        binding.imageTagsTextView.visibility = View.GONE
    }
}