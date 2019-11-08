package com.marxist.android.ui.activities

import android.os.Bundle
import com.marxist.android.R
import com.marxist.android.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_lottie_thanks.*

class LottieThankActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lottie_thanks)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}