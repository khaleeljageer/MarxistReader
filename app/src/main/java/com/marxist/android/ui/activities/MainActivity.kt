package com.marxist.android.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.marxist.android.R
import com.marxist.android.model.ConnectivityType
import com.marxist.android.model.DarkModeChanged
import com.marxist.android.model.NetWorkMessage
import com.marxist.android.ui.base.BaseActivity
import com.marxist.android.utils.PrintLog
import com.marxist.android.utils.RxBus
import com.marxist.android.utils.network.NetworkSchedulerService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar_widget.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_feeds,
                R.id.navigation_ebook,
                R.id.navigation_bookmarks,
                R.id.navigation_saved,
                R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        RxBus.subscribe({
            if (it is NetWorkMessage) {
                displayMaterialSnackBar(it.message, it.type)
            } else if (it is DarkModeChanged) {
                Handler().post {
                    recreate()
                }
            }
        }, {
            PrintLog.debug("Marxist", "$it")
        })
    }

    private fun displayMaterialSnackBar(
        message: String,
        type: ConnectivityType
    ) {
        val snackBar = Snackbar.make(
            container2,
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

    override fun onStart() {
        super.onStart()
        try {
            val startServiceIntent = Intent(this@MainActivity, NetworkSchedulerService::class.java)
            startService(startServiceIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        stopService(Intent(this@MainActivity, NetworkSchedulerService::class.java))
        super.onStop()
    }
}
