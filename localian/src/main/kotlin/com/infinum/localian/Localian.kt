@file:Suppress("unused")

package com.infinum.localian

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import com.infinum.localian.Localian.setLocale
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
public object Localian {

    private lateinit var cache: Cache

    private var locale: Locale = Locale.getDefault()

    private var systemLocale: Locale = Locale.getDefault()

    private val delegate: LocaleDelegate = LocaleDelegate()

    @JvmStatic
    @JvmOverloads
    public fun run(
        application: Application,
        locale: Locale = Locale.getDefault(),
        cache: Cache = PreferenceCache(application, locale)
    ) {
        this.locale = locale
        this.cache = cache

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

        update(
            application,
            when (cache.isFollowingSystemLocale()) {
                true -> systemLocale
                false -> cache.get()
            }
        )
    }

    @JvmStatic
    public fun setLocale(context: Context, locale: Locale) {
        this.locale = locale
        if (this::cache.isInitialized.not()) {
            this.cache = PreferenceCache(context, locale)
        }
        cache.followSystemLocale(false)
        update(context, locale)
    }

    @JvmStatic
    public fun getLocale(context: Context): Locale {
        if (this::cache.isInitialized.not()) {
            this.cache = PreferenceCache(context, locale)
        }
        return cache.get()
    }

    @JvmStatic
    public fun followSystemLocale(context: Context) {
        if (this::cache.isInitialized.not()) {
            this.cache = PreferenceCache(context, locale)
        }
        cache.followSystemLocale(true)
        update(context, systemLocale)
    }

    @JvmStatic
    public fun isFollowingSystemLocale(context: Context): Boolean {
        if (this::cache.isInitialized.not()) {
            this.cache = PreferenceCache(context, locale)
        }
        return cache.isFollowingSystemLocale()
    }

    private fun applyForActivity(activity: Activity) {
        delegate.updateLocale(activity, cache.get())
        activity.resetTitle()
    }

    private fun processConfigurationChange(context: Context, configuration: Configuration) {
        systemLocale = configuration.getLocaleCompat()
        when (cache.isFollowingSystemLocale()) {
            true -> update(context, systemLocale)
            false -> delegate.updateLocale(context, cache.get())
        }
    }

    private fun update(context: Context, locale: Locale) {
        cache.persist(locale)
        delegate.updateLocale(context, locale)
    }

    /**
     *  Interface for storing a Locale and its data.
     */
    public interface Cache {

        public fun get(): Locale

        public fun persist(locale: Locale)

        public fun followSystemLocale(value: Boolean)

        public fun isFollowingSystemLocale(): Boolean
    }
}
