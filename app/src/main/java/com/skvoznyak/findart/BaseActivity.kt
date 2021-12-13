package com.skvoznyak.findart

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.skvoznyak.findart.databinding.ActivityMainBinding
import com.skvoznyak.findart.databinding.LayoutToolbarBinding

open class BaseActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addActivity()
        addToolbar()
    }

    protected open fun addActivity() {
        val mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
    }

    protected open fun addToolbar() {
        val toolbarBinding = LayoutToolbarBinding.inflate(layoutInflater)
        addContentView(
            toolbarBinding.root,
            ViewGroup.LayoutParams(
                ViewGroup
                    .LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        setSupportActionBar(toolbarBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                this.finish()
                true
            }
            R.id.action_bookmarks -> {
                val intent = Intent(this@BaseActivity, PicturesListActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    protected fun showNotFound() {
        val imageView = findViewById<View>(R.id.not_found_picture)
        imageView?.visibility = View.VISIBLE
    }

    protected fun showToolbar(flag: Boolean) {
        val imageView = findViewById<View>(R.id.appBarLayout)
        if (flag) {
            imageView?.visibility = View.VISIBLE
        } else {
            imageView?.visibility = View.INVISIBLE
        }
    }

    protected fun setStatusBarColor(colorInt: Int) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, colorInt)
    }
}
