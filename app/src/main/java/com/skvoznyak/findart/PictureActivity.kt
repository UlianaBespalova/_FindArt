package com.skvoznyak.findart

import android.os.Bundle
import android.content.Intent
import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.os.Build
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import com.skvoznyak.findart.databinding.PictureScreenBinding
import com.google.gson.GsonBuilder
import com.skvoznyak.findart.model.Picture
import com.skvoznyak.findart.model.StorageManager
import com.squareup.picasso.Picasso


class PictureActivity : BaseActivity() {

    private lateinit var pictureBinding: PictureScreenBinding
    private var varMenu : Menu? = null
    private var picture: Picture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContent()

        val pictureJson = intent.extras?.get("picture") as String
        val builder = GsonBuilder()
        val gson = builder.create()
        picture = gson.fromJson(pictureJson, Picture::class.java)

        if (picture != null) {
            setData(picture!!)
            pictureBinding.pictureImage.setOnClickListener {
                openImage(picture!!.image)
            }
        }
    }

    private fun setData(picture: Picture) {
        pictureBinding.pictureTitle.text = picture.title
        pictureBinding.picturePainter.text = picture.painter
        pictureBinding.pictureYear.text = picture.year
        val text = "\t\t\t\t${picture.text.replace("*", "\n\n\t\t\t\t")}\n\n"
        pictureBinding.pictureText.text = text

        Picasso.with(this@PictureActivity).load(picture.image)
            .into(pictureBinding.pictureImage)
    }

    private fun openImage(image: String) {
        var bundle : Bundle? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            val v = pictureBinding.pictureImage
            val options = android.app.ActivityOptions.makeSceneTransitionAnimation(
                this@PictureActivity, v, "open_image")
            bundle = options.toBundle()
        }
        val intent = Intent(this@PictureActivity, FullScreenActivity::class.java)
        intent.putExtra("image", image)
        if (bundle == null) {
            startActivity(intent)
        } else {
            startActivity(intent, bundle)
        }
    }

    private fun addContent() {
        pictureBinding = PictureScreenBinding.inflate(layoutInflater)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            pictureBinding.pictureText.justificationMode = JUSTIFICATION_MODE_INTER_WORD
        }
        addContentView(
            pictureBinding.root, ViewGroup.LayoutParams(
                ViewGroup
                    .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    private fun addBookmark() {
        if (picture != null) {
            StorageManager.write(picture!!.title, picture!!)
        }
    }

    private fun deleteBookmark() {
        if (picture != null) {
            StorageManager.delete(picture!!.title)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        varMenu = menu

        fun setMenuItem(isAdded: Boolean) {
            if (isAdded) { menu?.findItem(R.id.added_bookmark)?.isVisible = true }
            else { menu?.findItem(R.id.add_bookmark)?.isVisible = true }
        }
        if (picture != null) {
            StorageManager.contains(picture!!.title, ::setMenuItem)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId) {
            android.R.id.home -> {
                this.finish()
                true
            }
            R.id.action_bookmarks -> {
                val intent = Intent(this@PictureActivity, PicturesListActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.add_bookmark -> {
                item.isVisible = false
                varMenu?.findItem(R.id.added_bookmark)?.isVisible = true
                addBookmark()
                true
            }
            R.id.added_bookmark -> {
                item.isVisible = false
                varMenu?.findItem(R.id.add_bookmark)?.isVisible = true
                deleteBookmark()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}