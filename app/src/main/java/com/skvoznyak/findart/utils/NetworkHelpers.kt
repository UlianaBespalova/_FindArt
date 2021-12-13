package com.skvoznyak.findart.utils

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.view.Gravity
import android.widget.Toast
import com.skvoznyak.findart.R


fun isOnline(context: Context) : Boolean {
    val cm : ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (cm.activeNetworkInfo == null) {
        return false
    }
    return true
}

fun noConnection(context: Context, darkFlag: Boolean = false) {
    val text = "Нет интернета"
    val duration = Toast.LENGTH_SHORT
    val toast = Toast.makeText(context, text, duration)
    toast.setGravity(Gravity.CENTER, 0, 0)

    if (darkFlag) {
        val view = toast.view
        view?.setBackgroundResource(R.color.transparent_dark)
    }
    toast.show()
}
