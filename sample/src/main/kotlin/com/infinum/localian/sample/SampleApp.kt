package com.infinum.localian.sample

import android.app.Application
import com.infinum.localian.Localian
import java.util.Locale

class SampleApp : Application() {

    companion object {
        val CROATIAN = Locale("hr", "HR")
        val ENGLISH = Locale("en", "US")
        val GERMAN = Locale("de", "DE")
    }

    override fun onCreate() {
        super.onCreate()

        Localian.run(application = this, locale = ENGLISH)
    }
}
