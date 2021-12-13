package com.skvoznyak.findart

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.pacoworks.rxpaper2.RxPaperBook
import com.skvoznyak.findart.utils.SharedPref

class FindArtApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPref.setContext(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode())
        RxPaperBook.init(this)
    }
}
