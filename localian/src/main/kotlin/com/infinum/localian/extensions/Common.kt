package com.infinum.localian.extensions

import android.os.Build
import android.webkit.CookieManager

internal fun isAtLeastSdk(versionCode: Int): Boolean =
    Build.VERSION.SDK_INT >= versionCode

@Suppress("TooGenericExceptionCaught", "SwallowedException")
internal fun isWebViewEnabled(): Boolean =
    try {
        CookieManager.getInstance()
        true
    } catch (e: Exception) {
        false
    }
