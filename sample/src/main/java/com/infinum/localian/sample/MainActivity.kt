package com.infinum.localian.sample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.infinum.localian.LocalianWebViewPatcher
import com.infinum.localian.sample.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val patcher = LocalianWebViewPatcher(this)

        with(viewBinding) {
            helloView.text = getString(R.string.hello, SimpleDateFormat.getDateInstance().format(Date()))
            showChild.setOnClickListener {
                startActivity(Intent(it.context, ChildActivity::class.java))
            }
            showWebView.setOnClickListener {
                patcher.patch()
                startActivity(Intent(it.context, WebViewActivity::class.java))
            }
            showSettings.setOnClickListener {
                startActivity(Intent(it.context, SettingsActivity::class.java))
            }
        }
    }
}
