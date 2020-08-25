package com.infinum.localian

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.annotation.RequiresApi
import com.infinum.localian.extensions.getLocaleCompat
import com.infinum.localian.extensions.isAtLeastSdk
import java.util.Locale

internal class LocaleDelegate {

    internal fun updateLocale(context: Context, locale: Locale) {
        updateResources(context, locale)
        val applicationContext = context.applicationContext
        if (applicationContext !== context) {
            updateResources(applicationContext, locale)
        }
    }

    @Suppress("DEPRECATION")
    private fun updateResources(context: Context, locale: Locale) {
        Locale.setDefault(locale)

        val resources = context.resources
        val currentLocale = resources.configuration.getLocaleCompat()

        if (currentLocale != locale) {
            val config = Configuration(resources.configuration)
            when {
                isAtLeastSdk(Build.VERSION_CODES.N) -> setLocaleNew(config, locale)
                isAtLeastSdk(Build.VERSION_CODES.JELLY_BEAN_MR1) -> setLocaleLegacy(config, locale)
                else -> setLocale(config, locale)
            }
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }

    @Suppress("SpreadOperator")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun setLocaleNew(config: Configuration, locale: Locale) {
        val defaultLocales = LocaleList.getDefault()

        config.setLocales(
            LocaleList(
                *linkedSetOf(locale)
                    .apply { addAll(List<Locale>(defaultLocales.size()) { defaultLocales[it] }) }
                    .toTypedArray()
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun setLocaleLegacy(config: Configuration, locale: Locale) {
        config.setLocale(locale)
    }

    @Suppress("DEPRECATION")
    private fun setLocale(config: Configuration, locale: Locale) {
        config.locale = locale
    }
}
