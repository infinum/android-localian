package com.infinum.localian.sample

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.infinum.localian.Localian
import com.infinum.localian.sample.LocalianApp.Companion.CROATIAN
import com.infinum.localian.sample.LocalianApp.Companion.ENGLISH
import com.infinum.localian.sample.LocalianApp.Companion.GERMAN
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

    private fun restart() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))

        Toast.makeText(this, "Activity restarted", Toast.LENGTH_SHORT).show()
    }
}
