package com.example.howie

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.view.Window
import androidx.core.content.ContextCompat

fun setupActivityColors(resources: Resources, window: Window, applicationContext: Context) {
    when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_YES -> {
            window.statusBarColor =
                ContextCompat.getColor(applicationContext, R.color.statusBarColorDark)
            window.navigationBarColor =
                ContextCompat.getColor(applicationContext, R.color.navigationBarColorDark)
        }
        Configuration.UI_MODE_NIGHT_NO -> {
            window.statusBarColor = Color.WHITE
            window.navigationBarColor =
                ContextCompat.getColor(applicationContext, R.color.navigationBarColorLight)
        }
    }
}