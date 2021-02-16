package com.infinum.localian.sample

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.infinum.localian.Localian
import com.infinum.localian.sample.SampleApp.Companion.CROATIAN
import com.infinum.localian.sample.SampleApp.Companion.ENGLISH
import com.infinum.localian.sample.SampleApp.Companion.GERMAN
import com.infinum.localian.sample.databinding.ActivitySettingsBinding
import java.util.Locale

class SettingsActivity : BaseActivity() {

    private lateinit var viewBinding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        with(viewBinding) {
            en.setOnClickListener {
                setNewLocale(ENGLISH)
            }
            hr.setOnClickListener {
                setNewLocale(CROATIAN)
            }
            de.setOnClickListener {
                setNewLocale(GERMAN)
            }
            systemLocale.setOnClickListener {
                followSystemLocale()
            }
        }

    }

    private fun setNewLocale(locale: Locale) {
        Localian.setLocale(this, locale) { restart() }
    }

    private fun followSystemLocale() {
        Localian.followSystemLocale(this) { restart() }
    }

    private fun restart() =
        startActivity(
            Intent(this, MainActivity::class.java)
                .apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                }
        ).also {
            Toast.makeText(this, getString(R.string.restarted), Toast.LENGTH_SHORT).show()
        }
}
