package com.infinum.localian.sample

import android.app.LocaleManager
import android.os.Bundle
import android.os.LocaleList
import com.infinum.localian.sample.Languages.CROATIAN
import com.infinum.localian.sample.Languages.ENGLISH
import com.infinum.localian.sample.Languages.GERMAN
import com.infinum.localian.sample.databinding.ActivitySettingsBinding

class SettingsActivity : BaseActivity() {

    private lateinit var viewBinding: ActivitySettingsBinding

    private val localeManager: LocaleManager
        get() = getSystemService(LocaleManager::class.java)

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
        localeManager.applicationLocales = LocaleList.forLanguageTags(languageTag)
    }

    private fun followSystemLocale() {
        localeManager.applicationLocales = LocaleList.getEmptyLocaleList()
    }
}
