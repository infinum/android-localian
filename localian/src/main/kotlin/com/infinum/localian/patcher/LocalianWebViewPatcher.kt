package com.infinum.localian.patcher

import android.content.Context
import android.webkit.WebView
import androidx.annotation.MainThread
import com.infinum.localian.extensions.isWebViewEnabled

/**
 * LocalianWebViewPatcher implements a workaround that fixes the unwanted side effect while
 * using a [WebView] introduced in Android N.
 *
 * The very first creation of a [WebView], programmatically
 * or via inflation, resets an application Locale to the system default.
 * More on that: https://issuetracker.google.com/issues/37113860
 *
 * @constructor Create a new instance with a mandatory [Context] parameter.
 */
public class LocalianWebViewPatcher(
    private val context: Context
) {
    private var semaphore = true

    /**
     * Invoke on demand to patch after first [WebView] is created.
     * This method is behind a semaphore, it will execute its implementation only once per class lifetime.
     * A part of the method implementation is an internal check if [WebView] is installed and enabled on the device.
     * Must be run on [MainThread].
     */
    @MainThread
    public fun patch() {
        if (semaphore) {
            semaphore = false
            if (isWebViewEnabled()) WebView(context).destroy()

            WebViewPatcher(context)()
        }
    }
}
