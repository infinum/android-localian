package com.infinum.localian

import android.content.Context
import android.webkit.WebView
import androidx.startup.Initializer
import com.infinum.localian.extensions.languageTag
import java.util.Locale

internal class LocalianInitializer : Initializer<Class<LocalianInitializer>> {

    override fun create(context: Context): Class<LocalianInitializer> {
        WebView(context).destroy()

        context.languageTag()
            ?.let {
                Localian.setLocale(context, Locale.forLanguageTag(it))
            } ?: Localian.setLocale(context, Localian.getLocale(context))

        return LocalianInitializer::class.java
    }

    override fun dependencies(): List<Class<out Initializer<*>>> =
        listOf()
}
