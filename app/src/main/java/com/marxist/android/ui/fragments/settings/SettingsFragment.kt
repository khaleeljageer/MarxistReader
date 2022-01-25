package com.marxist.android.ui.fragments.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.marxist.android.R
import com.marxist.android.ui.activities.AboutActivity
import com.marxist.android.ui.activities.FeedBackActivity
import com.marxist.android.ui.activities.LottieThankActivity
import com.marxist.android.utils.AppConstants

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = AppConstants.SharedPreference.FILE_NAME
        addPreferencesFromResource(R.xml.root_preferences)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        return when (preference!!.key) {
            getString(R.string.about_us) -> {
                startActivity(Intent(mContext, AboutActivity::class.java))
                true
            }
            getString(R.string.feed_back) -> {
                startActivity(Intent(mContext, FeedBackActivity::class.java))
                true
            }
            getString(R.string.lottie_files_credit) -> {
                startActivity(Intent(mContext, LottieThankActivity::class.java))
                true
            }
            getString(R.string.terms_of_use) -> {
                "https://marxistreader.app/terms-conditions/".openUrl()
                true
            }
            getString(R.string.privacy_policy) -> {
                "https://marxistreader.app/privacy-policy/".openUrl()
                true
            }
            else -> {
                super.onPreferenceTreeClick(preference)
            }
        }
    }

    private fun String.openUrl() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(this))
        startActivity(browserIntent)
    }

    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}