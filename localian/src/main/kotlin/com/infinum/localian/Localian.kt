package com.infinum.localian

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import com.infinum.localian.cache.Cache
import com.infinum.localian.cache.PreferenceCache
import com.infinum.localian.callbacks.ActivityCallbacks
import com.infinum.localian.callbacks.ApplicationCallbacks
import com.infinum.localian.extensions.getLocaleCompat
import com.infinum.localian.extensions.resetTitle
import java.util.Locale

/**
 * Localian is a tool to manage your application locale and language.
 *
 * Once you set a desired locale using [setLocale] method, Localian will enforce your application
 * to provide correctly localized data via [Resources] class.
 */
class Localian private constructor(
    private val application: Application,
    private val cache: Cache
) {

    companion object {

        private var INSTANCE: Localian? = null

        @JvmStatic
        fun run(
            application: Application,
            locale: Locale = Locale.getDefault(),
            cache: Cache = PreferenceCache(application, locale)
        ) {
            if (INSTANCE == null) {
                INSTANCE = Localian(application, cache)
                INSTANCE?.runInternal()
            } else {
                INSTANCE?.runInternal()
            }
        }

        @JvmStatic
        fun setLocale(context: Context, locale: Locale) {
            INSTANCE?.setLocaleInternal(context, locale) ?: println("Localian is not initialised.")
        }

        @JvmStatic
        fun getLocale(): Locale =
            INSTANCE?.getLocaleInternal() ?: run {
                println("Localian is not initialised.")
                Locale.getDefault()
            }

        @JvmStatic
        fun followSystemLocale(context: Context) {
            INSTANCE?.followSystemLocaleInternal(context) ?: println("Localian is not initialised.")
        }

        @JvmStatic
        fun isFollowingSystemLocale() =
            INSTANCE?.isFollowingSystemLocaleInternal() ?: run {
                println("Localian is not initialised.")
                false
            }
    }

    private var systemLocale: Locale = Locale.getDefault()

    private val delegate: LocaleDelegate = LocaleDelegate()

    private fun runInternal() {
        application.registerActivityLifecycleCallbacks(
            ActivityCallbacks {
                applyForActivity(it)
            }
        )
        application.registerComponentCallbacks(
            ApplicationCallbacks {
                processConfigurationChange(application, it)
            }
        )
        persistAndApply(
            application,
            when (cache.isFollowingSystemLocale()) {
                true -> systemLocale
                false -> cache.get()
            }
        )
    }

    private fun setLocaleInternal(context: Context, locale: Locale) {
        cache.followSystemLocale(false)
        persistAndApply(context, locale)
    }

    private fun getLocaleInternal(): Locale =
        cache.get()

    private fun followSystemLocaleInternal(context: Context) {
        cache.followSystemLocale(true)
        persistAndApply(context, systemLocale)
    }

    private fun isFollowingSystemLocaleInternal() =
        cache.isFollowingSystemLocale()

    private fun applyForActivity(activity: Activity) {
        applyLocale(activity)
        activity.resetTitle()
    }

    private fun processConfigurationChange(context: Context, config: Configuration) {
        systemLocale = config.getLocaleCompat()
        when (cache.isFollowingSystemLocale()) {
            true -> persistAndApply(context, systemLocale)
            false -> applyLocale(context)
        }
    }

    private fun persistAndApply(context: Context, locale: Locale) {
        cache.persist(locale)
        delegate.updateLocale(context, locale)
    }

    private fun applyLocale(context: Context) =
        delegate.updateLocale(context, cache.get())
}
