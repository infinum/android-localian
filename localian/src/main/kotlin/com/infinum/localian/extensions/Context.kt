package com.infinum.localian.extensions

import android.content.Context
import android.content.pm.PackageManager.GET_META_DATA

private const val KEY_INITIAL_LANGUAGE_TAG: String =
    "com.infinum.localian.initial_locale_language_tag"

internal fun Context.languageTag(): String? =
    packageManager
        .getApplicationInfo(
            packageName,
            GET_META_DATA
        )
        .metaData
        .getString(KEY_INITIAL_LANGUAGE_TAG, null)
        ?.takeIf { it.isNotBlank() }
