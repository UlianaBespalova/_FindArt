package com.skvoznyak.findart

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.pacoworks.rxpaper2.RxPaperBook
import com.skvoznyak.findart.utils.NightModeHelper

class FindArtApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NightModeHelper.setContext(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode())
        RxPaperBook.init(this)
    }
}
