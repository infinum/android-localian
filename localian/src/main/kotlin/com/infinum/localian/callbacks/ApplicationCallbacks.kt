package com.infinum.localian.callbacks

import android.content.ComponentCallbacks
import android.content.res.Configuration

internal class ApplicationCallbacks(
    private val callback: (Configuration) -> Unit
) : ComponentCallbacks {

    override fun onConfigurationChanged(newConfig: Configuration) =
        callback.invoke(newConfig)

    override fun onLowMemory() = Unit
}
