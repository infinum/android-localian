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
    } catch (exception: IllegalArgumentException) {
        // https://bugs.chromium.org/p/chromium/issues/detail?id=559720
        false
    } catch (exception: Exception) {
        // We cannot catch MissingWebViewPackageException as it is in a private/system API class.
        false
    }
