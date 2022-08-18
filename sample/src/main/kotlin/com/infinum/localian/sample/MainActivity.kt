package com.infinum.localian.sample

import android.content.Intent
import android.os.Bundle
import android.webkit.CookieManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.infinum.localian.sample.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        with(viewBinding) {
            helloView.text =
                getString(R.string.hello, SimpleDateFormat.getDateInstance().format(Date()))
            showChild.setOnClickListener {
                startActivity(Intent(it.context, ChildActivity::class.java))
            }
            showWebView.setOnClickListener {
                if (isWebViewEnabled()) {
                    startActivity(Intent(it.context, WebViewActivity::class.java))
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "WebView missing or disabled.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            showSettings.setOnClickListener {
                startActivity(Intent(it.context, SettingsActivity::class.java))
            }
        }
    }

    /**
     * Crude but proven way to test if WebView is missing or disabled.
     * It can be tested on debug devices buy running commands in terminal to enable or disable:
     *  adb shell pm enable com.google.android.webview
     *  adb shell pm disable-user --user 0 com.google.android.webview
     */
    private fun isWebViewEnabled(): Boolean =
        try {
            CookieManager.getInstance()
            true
        } catch (exception: IllegalArgumentException) {
            // https://bugs.chromium.org/p/chromium/issues/detail?id=559720
            false
        } catch (exception: Exception) {
            // We cannot catch MissingWebViewPackageException as it is in a private/system API class.
            false
        }
}
