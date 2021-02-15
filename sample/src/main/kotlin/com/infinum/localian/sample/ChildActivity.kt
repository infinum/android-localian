package com.infinum.localian.sample

import android.os.Bundle
import com.infinum.localian.sample.databinding.ActivityChildBinding

class ChildActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityChildBinding.inflate(layoutInflater).root)
    }
}
