package com.infinum.localian.extensions

import android.app.Activity
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_META_DATA

@Suppress("PrintStackTrace")
internal fun Activity.resetTitle() =
    try {
        val info = packageManager.getActivityInfo(componentName, GET_META_DATA)
        @Suppress("RedundantUnitExpression")
        when {
            info.labelRes != 0 -> setTitle(info.labelRes)
            else -> Unit
        }
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
