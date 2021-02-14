package com.infinum.localian

import android.content.Context
import android.webkit.WebView
import androidx.startup.Initializer

internal class LocalianInitializer : Initializer<Class<LocalianInitializer>> {

    override fun create(context: Context): Class<LocalianInitializer> {
        WebView(context).destroy()
        Localian.setLocale(context, Localian.getLocale(context))
        return LocalianInitializer::class.java
    }

    override fun dependencies(): List<Class<out Initializer<*>>> =
        listOf()
}
