package com.marxist.android.ui.activities

import android.os.Bundle
import android.view.MenuItem
import com.marxist.android.R
import com.marxist.android.databinding.ActivityLottieThanksBinding
import com.marxist.android.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LottieThankActivity : BaseActivity() {

    private val binding by lazy {
        ActivityLottieThanksBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = getString(R.string.lottie_credits)
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }
}