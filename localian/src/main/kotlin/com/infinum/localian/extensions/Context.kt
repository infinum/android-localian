package com.infinum.localian.extensions

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_META_DATA
import android.os.Build

private const val KEY_INITIAL_LANGUAGE_TAG: String =
    "com.infinum.localian.initial_locale_language_tag"

private const val KEY_FOLLOW_SYSTEM_LOCALE: String =
    "com.infinum.localian.follow_system_locale"

internal fun Context.languageTagMetadata(): String? =
    info.metaData
        .getString(KEY_INITIAL_LANGUAGE_TAG, null)
        ?.takeIf { it.isNotBlank() }

internal fun Context.followSystemLocaleMetadata(): Boolean =
    info.metaData
        .getBoolean(KEY_FOLLOW_SYSTEM_LOCALE, false)

@Suppress("DEPRECATION")
private val Context.info: ApplicationInfo
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager
            .getApplicationInfo(
                packageName,
                PackageManager.ApplicationInfoFlags.of(GET_META_DATA.toLong())
            )
    } else {
        packageManager
            .getApplicationInfo(
                packageName,
                GET_META_DATA
            )
    }
