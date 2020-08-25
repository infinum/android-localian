package com.infinum.localian.cache

import java.util.Locale

/**
 *  Interface for storing a Locale and its data.
 */
interface Cache {

    fun get(): Locale

    fun persist(locale: Locale)

    fun followSystemLocale(value: Boolean)

    fun isFollowingSystemLocale(): Boolean
}
