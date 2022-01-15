package com.marxist.android.ui.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.marxist.android.R
import com.marxist.android.databinding.ActivityAboutBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AboutActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityAboutBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = getString(R.string.about_us)
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp)
        }

        binding.txtAbout.text = HtmlCompat.fromHtml(
            getString(R.string.about_text),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }
}