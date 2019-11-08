package com.marxist.android.ui.fragments.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.marxist.android.R
import com.marxist.android.model.DarkModeChanged
import com.marxist.android.ui.activities.LottieThankActivity
import com.marxist.android.utils.AppConstants
import com.marxist.android.utils.RxBus

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
            getString(R.string.pref_key_dark_mode) -> {
                RxBus.publish(DarkModeChanged(""))
                true
            }
            getString(R.string.about_us) -> {
                true
            }
            getString(R.string.lottie_files_credit) -> {
                startActivity(Intent(mContext, LottieThankActivity::class.java))
                true
            }
            else -> {
                super.onPreferenceTreeClick(preference)
            }
        }
    }
}