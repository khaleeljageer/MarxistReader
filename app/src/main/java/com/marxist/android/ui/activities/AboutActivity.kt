package com.marxist.android.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.marxist.android.R
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        txtAbout.setHtml(getString(R.string.about_text))
    }
}