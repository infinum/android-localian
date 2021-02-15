package com.infinum.localian.sample

import android.os.Bundle
import com.infinum.localian.sample.databinding.ActivityWebviewBinding

class WebViewActivity : BaseActivity() {

    private lateinit var viewBinding: ActivityWebviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.webView.loadUrl("https://www.google.com/")
    }
}
