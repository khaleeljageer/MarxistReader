package com.marxist.android.ui.base

import android.content.SharedPreferences
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.marxist.android.R
import com.marxist.android.model.ConnectivityType
import com.marxist.android.utils.DeviceUtils
import org.koin.android.ext.android.inject

abstract class BaseActivity : AppCompatActivity() {
    val appPreference: SharedPreferences by inject()

    internal fun displayMaterialSnackBar(
        message: String,
        type: ConnectivityType,
        container: CoordinatorLayout,
        actionName: String = "",
        title: String = "",
        extra: String = ""
    ) {
        val snackBar = Snackbar.make(
            container,
            message,
            when {
                type == ConnectivityType.LOST -> Snackbar.LENGTH_INDEFINITE
                actionName.isNotEmpty() -> Snackbar.LENGTH_LONG
                else -> Snackbar.LENGTH_SHORT
            }
        )
        if (actionName.isNotEmpty()) {
            snackBar.setAction(actionName) {
                DeviceUtils.shareIntent(
                    title,
                    extra,
                    baseContext
                )
            }
        }

        val snackBarView = snackBar.view
        snackBarView.elevation = 6f
        snackBarView.background =
            ContextCompat.getDrawable(baseContext, R.drawable.rounded_snack_bar)
        snackBarView.layoutParams = assignMarginsToSnackBar(snackBarView)
        snackBar.show()
    }

    private fun assignMarginsToSnackBar(snackBarView: View): ViewGroup.LayoutParams {
        val marginSide = 16
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