package com.payback.pbpixabaykotlin

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Environment
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


object ImageSaver {

    fun saveImage(context: Context, url: String, imageName: String)  {

        Picasso.with(context)
            .load(url)
            .into(getTarget(context, imageName))
        }

    private fun getTarget(context: Context,imageName: String): Target {
        return object : Target{
            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                Thread(Runnable {
                    val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/" + APP_NAME
                    try {
                        val file = File(path, imageName + ".jpg")
                        file.parentFile.mkdir()
                        var stream = FileOutputStream(file)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                        stream.close()
                        Timber.i("file created")
                    }catch (e: IOException){
                         Timber.e("couldnt create file")
                    }
                }).start()
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
            override fun onBitmapFailed(errorDrawable: Drawable?) {}
        }
    }
}