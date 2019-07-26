package com.android.pixabay.view.ui


import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.pixabay.databinding.FragmentDetailBinding
import com.android.pixabay.viewmodel.ViewModelFactory
import com.android.pixabay.model.Hit
import com.android.pixabay.viewmodel.SharedViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_detail.*
import timber.log.Timber
import javax.inject.Inject


class DetailFragment : DaggerFragment() {
    lateinit var binding: FragmentDetailBinding
    lateinit var bhit: Hit
    private lateinit var viewModel: SharedViewModel
    //private var storagePermissionGranted: Boolean = false
    //lateinit var sharedPreferences: SharedPreferences
    //val USER_ASKED_STORAGE_PERMISSION_BEFORE = "0"
    //val MY_SHARED_PREFERENCES = "com.android.pixabay.prefs"

    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        viewModel = activity?.run {
//            ViewModelProviders.of(this, Injection.provideViewModelFactory()).get(SharedViewModel::class.java)
//        } ?: throw Exception("Invalid Activity")
        viewModel = activity?.run {
            ViewModelProviders.of(this, viewModelFactory).get(SharedViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        viewModel.selectedImage.observe(this, Observer<Hit> {
            //if (it == null) {
            bhit = it
            (activity as AppCompatActivity).supportActionBar?.title = bhit.user
            binding.hit = bhit
           // }
        })
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, com.android.pixabay.R.layout.fragment_detail, container, false)
        setHasOptionsMenu(true)

        binding.detailImageView.setOnViewTapListener { view, x, y ->
            when (likes_icon.visibility) {
                View.VISIBLE -> setDetailsInvisible()
                View.GONE -> setDetailsVisible()}
        }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(com.android.pixabay.R.menu.fragment_detail_action, menu)
    }

    private fun shareCurrentImage() {
        val shareStringBuilder = StringBuilder()
        shareStringBuilder.append(getString(com.android.pixabay.R.string.share_image_headline))
            .append("\n")
            .append(bhit.pageURL)

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareStringBuilder.toString())
        shareIntent.type = "text/plain"
        startActivity(Intent.createChooser(shareIntent, getString(com.android.pixabay.R.string.share_with)))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            com.android.pixabay.R.id.action_bar_share_icon -> {
                shareCurrentImage()
                return true
            }
            com.android.pixabay.R.id.action_bar_download_icon ->{

              /*  getExternalStoragePermission()
                if (storagePermissionGranted){
                    val imageName = bhit.id.toString()
                    viewModel.downloadImage(detail_imageView,imageName)
                    viewModel.downloadResult.observe(this, Observer {
                        it?.let {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    })
                }*/

                requestPermissionAndSave()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun downloadImage(){
        val imageName = bhit.id.toString()
        viewModel.downloadImage(detail_imageView,imageName)
        viewModel.downloadResult.observe(this, Observer {
            it?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        })
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

    /*companion object {
        private val PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1
    }*/

    /*private fun getExternalStoragePermission() {
        sharedPreferences = context!!.getSharedPreferences(MY_SHARED_PREFERENCES, MODE_PRIVATE)
        if (ContextCompat.checkSelfPermission(
                context!!, Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) {
            //Permission not granted

            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
            ) {
                //Can ask user for permission
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE)

            } else {
                val userAskedPermissionBefore:Boolean = sharedPreferences.getBoolean(
                    USER_ASKED_STORAGE_PERMISSION_BEFORE,
                    false
                )

                if (userAskedPermissionBefore) {
                    //If User was asked permission before and denied
                    val alertDialogBuilder = AlertDialog.Builder(context!!)

                    alertDialogBuilder.setTitle("Permission needed")
                    alertDialogBuilder.setMessage("Storage permission needed for downloading photos")
                    alertDialogBuilder.setPositiveButton("Open Setting",
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts(
                                "package", context!!.packageName,
                                null
                            )
                            intent.data = uri
                            startActivity(intent)
                            //this@MainActivity.startActivity(intent)

                        })
                    alertDialogBuilder.setNegativeButton("Cancel",
                        DialogInterface.OnClickListener { dialogInterface, i -> Timber.d("onClick: Cancelling") })

                    val dialog = alertDialogBuilder.create()
                    dialog.show()
                } else {
                    //If user is asked permission for first time
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE)

                    val editor = sharedPreferences.edit()
                    editor.putBoolean(USER_ASKED_STORAGE_PERMISSION_BEFORE, true)
                    editor.apply()
                }
            }

        } else {
            storagePermissionGranted = true
        }

    }*/

    /*override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        storagePermissionGranted = false
        when (requestCode) {
            PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Granted
                    storagePermissionGranted = true

                } else {
                    //Denied
                }
            }
        }
    }*/

    private fun requestPermissionAndSave() {
        Dexter.withActivity(activity)
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    downloadImage()
                }

                //TODO move title and message to string
                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    if (response.isPermanentlyDenied) {
                        val alertDialogBuilder = AlertDialog.Builder(context!!)
                        alertDialogBuilder.setTitle("Permission needed")
                        alertDialogBuilder.setMessage("Storage permission needed for downloading photos")
                        alertDialogBuilder.setPositiveButton("Open Setting",
                            DialogInterface.OnClickListener { dialogInterface, i ->
                                val intent = Intent()
                                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                val uri = Uri.fromParts("package", context!!.packageName,null)
                                intent.data = uri
                                startActivity(intent)
                            })
                        alertDialogBuilder.setNegativeButton("Cancel",
                            DialogInterface.OnClickListener { dialogInterface, i -> Timber.d("onClick: Cancelling") })

                        val dialog = alertDialogBuilder.create()
                        dialog.show()
                    }
                }
                override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                    token.continuePermissionRequest()
                }
            }).check()
    }
}