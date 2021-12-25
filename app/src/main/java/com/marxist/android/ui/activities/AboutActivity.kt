package com.marxist.android.ui.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.marxist.android.R
import com.marxist.android.databinding.ActivityAboutBinding
import dagger.hilt.android.AndroidEntryPoint
import org.sufficientlysecure.htmltextview.HtmlTextView


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

        binding.txtAbout.setHtmlFromString(
            getString(R.string.about_text),
            HtmlTextView.RemoteImageGetter()
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }
}