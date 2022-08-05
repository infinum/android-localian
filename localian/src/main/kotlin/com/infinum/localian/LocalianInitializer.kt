package com.infinum.localian

import android.content.Context
import android.webkit.WebView
import androidx.startup.Initializer
import com.infinum.localian.extensions.isWebViewEnabled
import com.infinum.localian.patcher.WebViewPatcher

internal class LocalianInitializer : Initializer<Class<LocalianInitializer>> {

    override fun create(context: Context): Class<LocalianInitializer> {
        takeIf { isWebViewEnabled() }?.let { WebView(context).destroy() }

        WebViewPatcher(context)()

        return LocalianInitializer::class.java
    }

    override fun dependencies(): List<Class<out Initializer<*>>> =
        listOf()
}
