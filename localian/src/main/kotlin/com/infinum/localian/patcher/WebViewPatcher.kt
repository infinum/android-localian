package com.infinum.localian.patcher

import android.content.Context
import com.infinum.localian.Localian
import com.infinum.localian.extensions.languageTag
import java.util.Locale

internal class WebViewPatcher(
    private val context: Context
) {

    operator fun invoke() =
        context.languageTag()
            ?.let {
                Localian.setLocale(context, Locale.forLanguageTag(it))
            } ?: Localian.setLocale(context, Localian.getLocale(context))
}
