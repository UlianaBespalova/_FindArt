package com.skvoznyak.findart

import android.os.Bundle
import android.view.MotionEvent
import com.skvoznyak.findart.databinding.PictureScreenFullscreenImageBinding
import com.github.chrisbanes.photoview.OnSingleFlingListener
import com.squareup.picasso.Picasso

class FullScreenActivity : BaseActivity() {

    private lateinit var pictureBinding: PictureScreenFullscreenImageBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val image = intent.extras?.get("image") as String
        if (image != "") {
            Picasso.with(this@FullScreenActivity).load(image)
                .into(pictureBinding.fullscreenImageView)
        }

        val mPhotoView = pictureBinding.fullscreenImageView
        mPhotoView.setOnSingleFlingListener(SingleFlingListener() {onBackPressed()})
    }

    private class SingleFlingListener(val callback: (() -> Unit)) : OnSingleFlingListener {
        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            callback()
            return true
        }
    }

    override fun addActivity() {
        pictureBinding = PictureScreenFullscreenImageBinding.inflate(layoutInflater)
        setContentView(pictureBinding.root)
    }

    override fun addToolbar() { }
}
