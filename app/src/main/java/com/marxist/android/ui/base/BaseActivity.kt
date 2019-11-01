package com.marxist.android.ui.base

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import com.marxist.android.R
import com.marxist.android.database.AppDatabase
import com.marxist.android.model.ConnectivityType
import com.marxist.android.utils.AppPreference
import com.marxist.android.utils.AppPreference.get

abstract class BaseActivity : AppCompatActivity() {
    lateinit var appPreference: SharedPreferences
    lateinit var appDatabase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appDatabase = AppDatabase.getAppDatabase(applicationContext)
        appPreference = AppPreference.customPrefs(baseContext)
        if (appPreference[getString(R.string.pref_key_dark_mode), false]) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.decorView.systemUiVisibility = 0
            }
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    internal fun displayMaterialSnackBar(
        message: String,
        type: ConnectivityType,
        container: CoordinatorLayout
    ) {
        val snackBar = Snackbar.make(
            container,
            message,
            if (type == ConnectivityType.LOST) Snackbar.LENGTH_INDEFINITE
            else
                Snackbar.LENGTH_SHORT
        )

        val snackBarView = snackBar.view
        snackBarView.layoutParams = assignMarginsToSnackBar(snackBarView)
        snackBar.show()
    }

    private fun assignMarginsToSnackBar(snackBarView: View): ViewGroup.LayoutParams {
        val marginSide = 10
        val params = snackBarView.layoutParams as CoordinatorLayout.LayoutParams

        params.setMargins(
            params.leftMargin + marginSide,
            params.topMargin,
            params.rightMargin + marginSide,
            params.bottomMargin + marginSide
        )

        return params
    }
}