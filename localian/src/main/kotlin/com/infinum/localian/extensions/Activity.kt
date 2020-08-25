package com.infinum.localian.extensions

import android.app.Activity
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_META_DATA

internal fun Activity.resetTitle() {
    try {
        val info = packageManager.getActivityInfo(componentName, GET_META_DATA)
        if (info.labelRes != 0) {
            setTitle(info.labelRes)
        }
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
}
