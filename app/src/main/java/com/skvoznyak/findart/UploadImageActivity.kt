package com.skvoznyak.findart

import android.os.Bundle
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.ViewGroup
import com.skvoznyak.findart.databinding.UploadScreenBinding
import com.skvoznyak.findart.utils.*
import java.util.*
import com.yalantis.ucrop.UCrop
import java.io.File
import androidx.core.content.ContextCompat
import com.yalantis.ucrop.UCropActivity


class UploadImageActivity : GetImage() {

    private lateinit var uploadImageBinding: UploadScreenBinding
    private var bm : Bitmap? = null
    private val tfliteModel = TfliteModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContent()

        val requestCode = intent.extras?.get("requestCode") as Int?
        val data = intent.extras?.get("data") as Intent?
        val currentPhotoPath = intent.extras?.get("currentPhotoPath") as String?

        if (requestCode != null) {
            val newCurrentImageUri = intent.extras?.get("currentImageUri")
            if (newCurrentImageUri != null) {
                currentImageUri = Uri.parse("$newCurrentImageUri")
            }
            processImage(requestCode, currentPhotoPath, data)
            if (bm != null) uploadImageBinding.uploadedImage.setImageBitmap(bm)
        }

        uploadImageBinding.buttonChooseAnother.setOnClickListener {
            selectImage()
        }

        uploadImageBinding.buttonEdit.setOnClickListener {
            startEditing()
        }

        uploadImageBinding.buttonFind.setOnClickListener {
            if (isOnline(this)) {
                findPicture()
            }
            else {
                noConnection(applicationContext)
            }
//            if (SharedPref.loadNightModeState()) {
//                SharedPref.setNightModeState(false)
//            } else {
//                SharedPref.setNightModeState(true)
//            }
        }
    }

    private fun startEditing() {
        if (bm == null || currentImageUri == null) {
            return
        }
        val destinationUri = Uri.fromFile(File(cacheDir, "IMG_FindArt_" + System.currentTimeMillis()))
        openCropActivity(currentImageUri!!, destinationUri, bm!!.height, bm!!.width)
    }


    fun openCropActivity(sourceUri: Uri, destinationUri: Uri, maxHeight: Int, maxWidth: Int) {
        try {
            var uCrop = UCrop.of(sourceUri, destinationUri)
                .withMaxResultSize(maxWidth, maxHeight)
            uCrop = advancedConfig(uCrop)
            uCrop.start(this)
        } catch (e: Exception) {
            Log.d("ivan", "UCrop: error while oppening activity")
            e.printStackTrace()
        }
    }

    private fun advancedConfig(uCrop: UCrop): UCrop? {
        val options = UCrop.Options()
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL)
        options.setToolbarColor(ContextCompat.getColor(this, R.color.ucrop_toolbar_color))
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.ucrop_status_bar_color))
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.ucrop_toolbar_text_color))
        options.setRootViewBackgroundColor(ContextCompat.getColor(this, R.color.ucrop_background_color))
        options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.color_secondary))
        return uCrop.withOptions(options)
    }

    private fun findPicture() {
        if (bm != null) {
            val vector = tfliteModel.imageToVector(bm!!, this)
            val intent =
                Intent(this@UploadImageActivity, SimilarPicturesListActivity::class.java)
            intent.putExtra("headerFlag", true)
            intent.putExtra("vector", vector)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            val imageUri = data?.let { UCrop.getOutput(it) }
            val imageView = uploadImageBinding.uploadedImage
            if (imageUri != null) {
                showImageFromUri(this, imageUri, imageView)
                bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun addContent() {
        uploadImageBinding = UploadScreenBinding.inflate(layoutInflater)
        addContentView(
            uploadImageBinding.root, ViewGroup.LayoutParams(
                ViewGroup
                    .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    private fun processImage(requestCode : Int, currentPhotoPath : String?, data : Intent?) {
        when (requestCode) {
            activityResCodeSelectFile -> {
                if (data != null) {
                    bm = onSelectFromGalleryResult(data)
                }
            }
            activityResCodeRequestCamera -> {
                if (currentPhotoPath != null) {
                    bm = BitmapFactory.decodeFile(currentPhotoPath)
                }
            }
        }
    }

    override fun onCaptureImageResult() : Bitmap {
        bm = super.onCaptureImageResult()
        uploadImageBinding.uploadedImage.setImageBitmap(bm)
        return bm as Bitmap
    }

    override fun onSelectFromGalleryResult(data: Intent?) : Bitmap? {
        bm = super.onSelectFromGalleryResult(data)
        if (bm != null) {
            uploadImageBinding.uploadedImage.setImageBitmap(bm)
        }
        return bm
    }
}
