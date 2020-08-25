package com.infinum.localian.cache

import java.util.Locale

/**
 * Implementation of cache that keeps data in memory.
 *
 * Useful for cases like instrumentation tests, where you don't want to persist any changes
 * to the application locale.
 */
class FakeCache : Cache {

    private var locale: Locale = Locale.getDefault()

    private var isFollowingSystemLocale = false

    override fun get(): Locale = locale

    override fun persist(locale: Locale) {
        this.locale = locale
    }

    override fun followSystemLocale(value: Boolean) {
        isFollowingSystemLocale = value
    }

    override fun isFollowingSystemLocale(): Boolean = isFollowingSystemLocale
}
