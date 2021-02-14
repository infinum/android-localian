package com.infinum.localian.extensions

import android.content.res.Configuration
import android.os.Build
import java.util.Locale

@Suppress("DEPRECATION")
internal fun Configuration.getLocaleCompat(): Locale =
    if (isAtLeastSdk(Build.VERSION_CODES.N)) {
        locales.get(0)
    } else {
        locale
    }
