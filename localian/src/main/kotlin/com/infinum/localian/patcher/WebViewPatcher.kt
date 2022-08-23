package com.infinum.localian.patcher

import android.content.Context
import com.infinum.localian.Localian
import com.infinum.localian.extensions.followSystemLocaleMetadata
import com.infinum.localian.extensions.languageTagMetadata
import java.util.Locale

internal class WebViewPatcher(
    private val context: Context
) {

    operator fun invoke() =
        if (context.followSystemLocaleMetadata().not()) {
            context.languageTagMetadata()
                ?.let {
                    Localian.setLocale(context, Locale.forLanguageTag(it))
                } ?: Localian.setLocale(context, Localian.getLocale(context))
        } else {
            Unit
        }
}
