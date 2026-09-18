package com.infinum.localian.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.infinum.localian.sample.Languages.CROATIAN
import com.infinum.localian.sample.Languages.ENGLISH
import com.infinum.localian.sample.Languages.GERMAN
import com.infinum.localian.sample.databinding.ActivitySettingsBinding

class SettingsActivity : BaseActivity() {

    private lateinit var viewBinding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        with(viewBinding) {
            en.setOnClickListener {
                setApplicationLanguage(ENGLISH)
            }
            hr.setOnClickListener {
                setApplicationLanguage(CROATIAN)
            }
            de.setOnClickListener {
                setApplicationLanguage(GERMAN)
            }
            systemLocale.setOnClickListener {
                followSystemLocale()
            }
        }
    }

    private fun setApplicationLanguage(languageTag: String) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag))
    }

    private fun followSystemLocale() {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
    }
}
