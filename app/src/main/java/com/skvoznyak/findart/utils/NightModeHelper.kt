package com.skvoznyak.findart.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

object NightModeHelper {
    private var mySharePref: SharedPreferences? = null

    fun setContext(context: Context) {
        mySharePref = context.getSharedPreferences("filename", Context.MODE_PRIVATE)
    }

    fun setNightModeState(state: Boolean) {
        if (mySharePref == null) return
        val editor: SharedPreferences.Editor = mySharePref!!.edit()
        editor.putBoolean("NightMode", state)
        editor.commit()
        if (state) {
            setNightMode()
        } else {
            setLightMode()
        }
    }

    fun loadNightModeState(): Boolean {
        if (mySharePref == null) return false
        return mySharePref!!.getBoolean("NightMode", false)
    }

    private fun setNightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    private fun setLightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}

fun isNightMode(uiMode: Int): Boolean {
    when (uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_YES -> {
            return true
        }
        else -> {
            return false
        }
    }
}
