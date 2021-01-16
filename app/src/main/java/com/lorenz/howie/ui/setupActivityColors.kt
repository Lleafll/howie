package com.lorenz.howie.ui

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.view.Window
import androidx.core.content.ContextCompat
import com.example.howie.R

fun setupActivityColors(resources: Resources, window: Window, applicationContext: Context) {
    if (isDarkMode(resources)) {
        window.statusBarColor =
            ContextCompat.getColor(applicationContext, R.color.statusBarColorDark)
        window.navigationBarColor =
            ContextCompat.getColor(applicationContext, R.color.navigationBarColorDark)
    } else {
        window.statusBarColor = Color.WHITE
        window.navigationBarColor =
            ContextCompat.getColor(applicationContext, R.color.navigationBarColorLight)

    }
}

fun isDarkMode(resources: Resources): Boolean {
    return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
}