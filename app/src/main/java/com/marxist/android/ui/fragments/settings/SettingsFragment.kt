package com.marxist.android.ui.fragments.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.marxist.android.R
import com.marxist.android.model.DarkModeChanged
import com.marxist.android.utils.AppConstants
import com.marxist.android.utils.PrintLog
import com.marxist.android.utils.RxBus

class SettingsFragment : PreferenceFragmentCompat() {
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
            else -> {
                super.onPreferenceTreeClick(preference)
            }
        }
    }
}