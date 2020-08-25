package com.infinum.localian.extensions

import android.os.Build

internal fun isAtLeastSdk(versionCode: Int): Boolean =
    Build.VERSION.SDK_INT >= versionCode
