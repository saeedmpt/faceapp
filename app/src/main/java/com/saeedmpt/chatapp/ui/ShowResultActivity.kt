package com.saeedmpt.chatapp.ui

import com.saeedmpt.chatapp.ui.base.BaseActivity
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.saeedmpt.chatapp.R
import com.saeedmpt.chatapp.databinding.ActivityShowResultBinding
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import androidx.core.content.FileProvider
import java.io.IOException
import android.content.Intent
import com.saeedmpt.chatapp.BuildConfig


class ShowResultActivity : BaseActivity() {
    private lateinit var resource2: Bitmap
    private lateinit var image2: String
    private lateinit var image1: String
    private var contInst: Activity? = null
    private lateinit var binding: ActivityShowResultBinding
    private val currentFragment = 0

    /*@JvmField
    var valueType: String? = null
    @JvmField
    var actionType: String? = null*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_result)
        contInst = this
        val intent = intent
        if (intent != null && intent.extras != null) {
            val extras = intent.extras
            val filterName = extras!!.getString("filterName").toString()
            binding.tvFilter.text = filterName
            image1 = extras.getString("image1").toString()
            image2 = extras.getString("image2").toString()
        }

        initiView()
    }

    private fun initiView() {
        showImage(image1, binding.ivImage1)
        showImage(image2, binding.ivImage2)
        binding.ivSaveGallery.setOnClickListener {
            if (::resource2.isInitialized) {
                saveToGallery(this,
                    resource2,
                    resources.getString(R.string.app_name))
            } else {
                Toast.makeText(this, "please wait for load image", Toast.LENGTH_SHORT).show()
            }
        }

        binding.ivShare.setOnClickListener {
            if (::resource2.isInitialized) {
                shareImageUri(saveImageToCache(resource2)!!)
            } else {
                Toast.makeText(this, "please wait for load image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showImage(url: String, iv: ImageView) {
        val options = RequestOptions()
            .error(R.drawable.ic_logo)
            .priority(Priority.HIGH)
        Glide.with(contInst!!)
            .asBitmap()
            .load(url)
            .apply(options)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(object : SimpleTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?,
                ) {
                    resource2 = resource
                    iv.setImageBitmap(resource)
                }
            }
            )
    }

    private fun saveToGallery(context: Context, bitmap: Bitmap, albumName: String) {
        val filename = "${System.currentTimeMillis()}.png"
        val write: (OutputStream) -> Boolean = {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH,
                    "${Environment.DIRECTORY_DCIM}/$albumName")
            }

            context.contentResolver.let {
                it.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)?.let { uri ->
                    it.openOutputStream(uri)?.let(write)
                }
            }
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    .toString() + File.separator + albumName
            val file = File(imagesDir)
            if (!file.exists()) {
                file.mkdir()
            }
            val image = File(imagesDir, filename)
            write(FileOutputStream(image))
        }
        Toast.makeText(this, "image saved", Toast.LENGTH_SHORT).show()
    }

    /**
     * Saves the image as PNG to the app's cache directory.
     * @param image Bitmap to save.
     * @return Uri of the saved file or null
     */
    private fun saveImageToCache(image: Bitmap): Uri? {
        val imagesFolder = File(cacheDir, "images")
        var uri: Uri? = null
        try {
            imagesFolder.mkdirs()
            val file = File(imagesFolder, "shared_image.png")
            val stream = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.PNG, 90, stream)
            stream.flush()
            stream.close()
            uri = FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.provider", file)
        } catch (e: IOException) {
            Log.d("TAG", "IOException while trying to write file for sharing: " + e.message)
        }
        return uri
    }

    /**
     * Shares the PNG image from Uri.
     * @param uri Uri of image to share.
     */
    private fun shareImageUri(uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "image/png"
        startActivity(intent)
    }
}