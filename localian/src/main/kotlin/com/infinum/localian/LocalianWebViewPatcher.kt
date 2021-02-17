package com.infinum.localian

import android.content.Context
import android.webkit.WebView
import androidx.annotation.MainThread

/**
 * LocalianWebViewPatcher implements a workaround that fixes the unwanted side effect while
 * using a [WebView] introduced in Android N.
 *
 * The very first creation of a [WebView], programmatically
 * or via inflation, resets an application Locale to the system default.
 * More on that: https://issuetracker.google.com/issues/37113860
 */
public class LocalianWebViewPatcher(
    private val context: Context
) {
    private var semaphore = true

    @MainThread
    public fun patch() {
        if (semaphore) {
            semaphore = false
            WebView(context).destroy()
            Localian.setLocale(context, Localian.getLocale(context))
        }
    }
}
