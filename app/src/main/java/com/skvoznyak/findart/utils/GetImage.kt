package com.skvoznyak.findart.utils

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skvoznyak.findart.BaseActivity
import com.skvoznyak.findart.R
import java.io.File
import java.io.IOException

open class GetImage : BaseActivity() {

    private val permissionsRequestReadExtStorage = 101
    protected val activityResCodeRequestCamera = 102
    protected val activityResCodeSelectFile = 103
    private val titleAddPhoto = "Добавить фото"
    private val titleSelectFile = "Выбрать файл"
    private val takePhoto = "Сделать фото"
    private val chooseFromGallery = "Выбрать из Галереи"
    private val cancel = "Отмена"
    private val actions = arrayOf<CharSequence>(chooseFromGallery, takePhoto, cancel)
    private var userChosenTask = ""
    private val fileName = "photo"
    private lateinit var storageDirectory: File
    private lateinit var imageFile: File
    lateinit var currentPhotoPath: String
    var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        imageFile = File.createTempFile(fileName, ".jpg", storageDirectory)
        currentPhotoPath = imageFile.absolutePath
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                activityResCodeSelectFile -> {
                    onSelectFromGalleryResult(data)
                    currentImageUri = data?.data
                }
                activityResCodeRequestCamera -> {
                    onCaptureImageResult()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permissionsRequestReadExtStorage -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    if (userChosenTask == takePhoto) {
                        cameraIntent()
                    } else if (userChosenTask == chooseFromGallery) {
                        galleryIntent()
                    }
                }
            }
        }
    }

    open fun onSelectFromGalleryResult(data: Intent?): Bitmap? {
        var bm: Bitmap? = null
        if (data != null) {
            try {
                bm = if (Build.VERSION.SDK_INT < 29) {
                    MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, data.data)
                } else {
                    val source = ImageDecoder.createSource(
                        applicationContext.contentResolver,
                        data.data!!
                    )
                    ImageDecoder.decodeBitmap(source)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return bm
    }

    open fun onCaptureImageResult(): Bitmap {
        return BitmapFactory.decodeFile(currentPhotoPath)
    }

    fun selectImage() {
        val builder = MaterialAlertDialogBuilder(this, R.style.DialogTheme)
        builder.setTitle(titleAddPhoto)
        builder.setItems(actions) { dialog, item ->
            val result = checkPermission(this)
            if (actions[item] == takePhoto) {
                userChosenTask = takePhoto
                if (result) {
                    cameraIntent()
                }
            } else if (actions[item] == chooseFromGallery) {
                userChosenTask = chooseFromGallery
                if (result) {
                    galleryIntent()
                }
            } else if (actions[item] == cancel) {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun cameraIntent() {
        val imageUri = FileProvider.getUriForFile(
            this@GetImage,
            "com.skvoznyak.findart.fileprovider",
            imageFile
        )
        currentImageUri = imageUri

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, activityResCodeRequestCamera)
    }

    private fun galleryIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, titleSelectFile),
            activityResCodeSelectFile
        )
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun checkPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat
                .checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat
                    .shouldShowRequestPermissionRationale(
                        context as Activity,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    val alertBuilder = MaterialAlertDialogBuilder(context, R.style.DialogTheme)
                    alertBuilder.setCancelable(true)
                    alertBuilder.setTitle("Требуется разрешение")
                    alertBuilder.setMessage(
                        "Для выполнения действия приложению необходим доступ к фото"
                    )
                    alertBuilder.setPositiveButton(android.R.string.yes) { _, _ ->
                        ActivityCompat.requestPermissions(
                            context,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            permissionsRequestReadExtStorage
                        )
                    }
                    val alert = alertBuilder.create()
                    alert.show()
                } else {
                    ActivityCompat.requestPermissions(
                        context,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        permissionsRequestReadExtStorage
                    )
                }
                return false
            } else {
                return true
            }
        } else {
            return true
        }
    }

    fun showImageFromUri(context: Context, imageUri: Uri, view: ImageView) {
        Log.d("ivan", "trying to show")
        val bm: Bitmap =
            MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri)
        view.setImageBitmap(bm)
    }
}
