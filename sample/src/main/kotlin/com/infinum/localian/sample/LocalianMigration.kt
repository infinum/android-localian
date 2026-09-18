package com.infinum.localian.sample

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import org.json.JSONException
import org.json.JSONObject

/**
 * Converts the language Localian saved into [AppCompatDelegate], once, and then forgets it.
 * Call it from the launcher activity, before the layout is inflated.
 */
object LocalianMigration {

    private const val TAG = "LocalianMigration"

    private const val PREFERENCES = "localian_preference"
    private const val KEY_CURRENT_LOCALE = "key_current_locale"
    private const val KEY_FOLLOW_SYSTEM_LOCALE = "key_follow_system_locale"

    private const val LANGUAGE = "language"
    private const val COUNTRY = "country"

    fun restoreSelectedLanguage(activity: Activity) {
        val preferences = activity.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        if (preferences.contains(KEY_CURRENT_LOCALE).not()) {
            return
        }

        val tag = when {
            preferences.getBoolean(KEY_FOLLOW_SYSTEM_LOCALE, false) -> null
            else -> preferences.getString(KEY_CURRENT_LOCALE, null)?.toLanguageTag()
        }
        preferences.edit().clear().apply()

        // Localian saved the locale on every start, so one matching the device language
        // is not a selection anyone made.
        val deviceLanguage = ConfigurationCompat.getLocales(activity.resources.configuration)[0]
            ?.language
        if (tag != null && tag.substringBefore("-") != deviceLanguage) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
        }
    }

    private fun String.toLanguageTag(): String? =
        try {
            JSONObject(this).let { json ->
                val language = json.optString(LANGUAGE)
                val country = json.optString(COUNTRY)
                when {
                    language.isEmpty() -> null
                    country.isEmpty() -> language
                    else -> "$language-$country"
                }
            }
        } catch (exception: JSONException) {
            Log.w(TAG, "Saved locale could not be read.", exception)
            null
        }
}
