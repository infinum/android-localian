package com.infinum.localian.extensions

import android.app.Activity
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_META_DATA
import android.os.Build

@Suppress("PrintStackTrace")
internal fun Activity.resetTitle() =
    try {
        @Suppress("DEPRECATION")
        val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getActivityInfo(
                componentName,
                PackageManager.ComponentInfoFlags.of(GET_META_DATA.toLong())
            )
        } else {
            packageManager.getActivityInfo(
                componentName,
                GET_META_DATA
            )
        }
        @Suppress("RedundantUnitExpression")
        when {
            info.labelRes != 0 -> setTitle(info.labelRes)
            else -> Unit
        }
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
