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
            val configuration = Configuration(resources.configuration)
            when {
                isAtLeastSdk(Build.VERSION_CODES.N) ->
                    setLocaleNew(configuration, locale)
                isAtLeastSdk(Build.VERSION_CODES.JELLY_BEAN_MR1) ->
                    setLocaleLegacy(configuration, locale)
                else -> setLocale(configuration, locale)
            }
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }
    }

    @Suppress("SpreadOperator")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun setLocaleNew(configuration: Configuration, locale: Locale) {
        val defaultLocales = LocaleList.getDefault()

        configuration.setLocales(
            LocaleList(
                *linkedSetOf(locale)
                    .apply { addAll(List<Locale>(defaultLocales.size()) { defaultLocales[it] }) }
                    .toTypedArray()
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun setLocaleLegacy(configuration: Configuration, locale: Locale) {
        configuration.setLocale(locale)
    }

    @Suppress("DEPRECATION")
    private fun setLocale(configuration: Configuration, locale: Locale) {
        configuration.locale = locale
    }
}
