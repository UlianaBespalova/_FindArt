package com.skvoznyak.findart

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skvoznyak.findart.model.Picture
import com.skvoznyak.findart.model.PictureRepository
import com.skvoznyak.findart.utils.isNightMode
import com.skvoznyak.findart.utils.isOnline
import com.skvoznyak.findart.utils.noConnection
import io.reactivex.android.schedulers.AndroidSchedulers

class SimilarPicturesListActivity : PicturesListActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val vector = intent.extras?.get("vector") as FloatArray
        if (vector.isNotEmpty()) {
            if (isOnline(this)) {
                if (savedInstanceState == null) {
                    showLoader()
                }
                val similarRequestDisposable = PictureRepository.getSimilarPictures(vector)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { images ->
                            if (images.isNotEmpty()) {
                                makePictureList(images)
                            } else { noResults() }
                        },
                        { err ->
                            Log.d("ivan", "request error: $err")
                            noResults()
                        }
                    )
                disposables.add(similarRequestDisposable)
            } else {
                noConnection(applicationContext)
                finish()
            }
        } else {
            noResults()
        }
    }

    private fun noResults() {
        hideLoader()

        val resultList: RecyclerView = findViewById(R.id.resultList)
        val headerAdapter = HeaderAdapter(resources.getString(R.string.no_results))
        resultList.adapter = headerAdapter
        resultList.layoutManager = LinearLayoutManager(this)
        showNotFound()
    }

    private fun makePictureList(images: List<Picture>) {
        Log.d("ivan", "Server: Success!")
        pictures = images
        setResultList(images)
    }

    private fun setResultList(images: List<Picture>) {
        val resultList: RecyclerView = findViewById(R.id.resultList)
        resultList.isNestedScrollingEnabled = true

        val headerAdapter = HeaderAdapter(resources.getString(R.string.best_results))
        val pictureAdapter = PictureAdapter(this, images, ::hideLoader, ::openPicture)
        resultList.adapter = ConcatAdapter(headerAdapter, pictureAdapter)
        resultList.layoutManager = LinearLayoutManager(this)
    }

    private fun showLoader() {
        showToolbar(false)
        if (!isNightMode(resources.configuration.uiMode)) {
            setStatusBarColor(R.color.status_bar_color)
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.listScreenContainer, LoadingFragment(), "LoadingFragment")
            .commit()
    }

    private fun hideLoader() {
        showToolbar(true)
        if (!isNightMode(resources.configuration.uiMode)) {
            setStatusBarColor(R.color.color_primary_variant)
        }
        val fragment = supportFragmentManager.findFragmentByTag("LoadingFragment")
        if (fragment != null && fragment.isVisible) {
            supportFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .hide(fragment)
                .commit()
        }
    }

    override fun createResultList() { }
    override fun addMenuTitle() { }
}
