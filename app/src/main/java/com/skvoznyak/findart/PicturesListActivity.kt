package com.skvoznyak.findart

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.skvoznyak.findart.databinding.ListScreenBinding
import com.skvoznyak.findart.model.Picture
import com.skvoznyak.findart.model.StorageManager
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable


open class PicturesListActivity : BaseActivity() {

    private lateinit var listScreenBinding: ListScreenBinding
    protected var pictures : List<Picture>? = null
    private var varMenu : Menu? = null
    protected val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContent()

        createResultList()
    }


    private fun makePictureList(picturesSingle: Single<List<Picture>>) {
        val picturesDisposable = picturesSingle
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ pics ->
                if (pics.isNotEmpty()) {
                    pictures = pics
                    setResultList()

                } else {
                    noResults()
                }
            }, { err ->
                noResults()

            })
        disposables.add(picturesDisposable)
    }

    private fun noResults() {
        val resultList:RecyclerView = findViewById(R.id.resultList)
        val headerAdapter = HeaderAdapter(resources.getString(R.string.no_bookmarks))
        resultList.adapter = headerAdapter
        resultList.layoutManager = LinearLayoutManager(this)
    }

    private fun setResultList() {
        if (pictures == null) return
        val resultList:RecyclerView = findViewById(R.id.resultList)
        resultList.isNestedScrollingEnabled = true

        val pictureAdapter = PictureAdapter(this, pictures!!, null, ::openPicture)
        resultList.adapter = pictureAdapter

        resultList.layoutManager = LinearLayoutManager(this)
    }

    open fun createResultList(){
        StorageManager.getAll(::makePictureList)
    }

    private fun addContent() {
        listScreenBinding = ListScreenBinding.inflate(layoutInflater)
        addContentView(
            listScreenBinding.root, ViewGroup.LayoutParams(
                ViewGroup
                    .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        varMenu = menu
        addMenuTitle()
        return true
    }

    open fun addMenuTitle() {
        varMenu?.findItem(R.id.bookmarks_title)?.isVisible = true
    }

    fun openPicture(title: String) {
        val picture = pictures?.firstOrNull{ it.title == title } ?: return

        val builder = GsonBuilder()
        val gson = builder.create()
        val intent =
            Intent(this@PicturesListActivity, PictureActivity::class.java)
        intent.putExtra("picture", gson.toJson(picture))
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }
}