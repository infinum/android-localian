package com.infinum.localian.cache

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject
import java.util.Locale

/**
 * Implementation of cache using [SharedPreferences].
 */
class PreferenceCache @JvmOverloads constructor(
    context: Context,
    private val defaultLocale: Locale = Locale.getDefault(),
    preferenceName: String = DEFAULT_PREFERENCE_NAME
) : Cache {

    companion object {
        private const val DEFAULT_PREFERENCE_NAME = "localian_preference"

        private const val KEY_FOLLOW_SYSTEM_LOCALE = "key_follow_system_locale"
        private const val KEY_CURRENT_LOCALE = "key_current_locale"

        private const val LANGUAGE = "language"
        private const val COUNTRY = "country"
        private const val VARIANT = "variant"
    }

    private val preferences: SharedPreferences = context.getSharedPreferences(
        preferenceName,
        Context.MODE_PRIVATE
    )

    override fun get(): Locale =
        preferences.getString(KEY_CURRENT_LOCALE, null)?.let {
            when (it.isBlank()) {
                true -> defaultLocale
                false -> JSONObject(it).let { json ->
                    Locale(
                        json.getString(LANGUAGE),
                        json.getString(COUNTRY),
                        json.getString(VARIANT)
                    )
                }
            }
        } ?: defaultLocale

    override fun persist(locale: Locale) =
        preferences.edit()
            .putString(
                KEY_CURRENT_LOCALE,
                JSONObject().apply {
                    put(LANGUAGE, locale.language)
                    put(COUNTRY, locale.country)
                    put(VARIANT, locale.variant)
                }.toString()
            )
            .apply()

    override fun followSystemLocale(value: Boolean) =
        preferences.edit()
            .putBoolean(
                KEY_FOLLOW_SYSTEM_LOCALE,
                value
            ).apply()

    override fun isFollowingSystemLocale(): Boolean =
        preferences.getBoolean(KEY_FOLLOW_SYSTEM_LOCALE, false)
}
