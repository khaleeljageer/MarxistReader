package com.marxist.android.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.marxist.android.R
import com.marxist.android.model.ConnectivityType
import com.marxist.android.model.DarkModeChanged
import com.marxist.android.model.NetWorkMessage
import com.marxist.android.model.ShowSnackBar
import com.marxist.android.ui.base.BaseActivity
import com.marxist.android.utils.PrintLog
import com.marxist.android.utils.RxBus
import com.marxist.android.utils.network.NetworkSchedulerService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar_widget.*

class MainActivity : BaseActivity() {

    companion object {
        const val PLAY_NEW_VIDEO = "com.marxist.android.ui.activities.PLAY_NEW_VIDEO"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        if (intent != null && intent.data != null) {
            PrintLog.debug("Khaleel", "Intent Data : ${intent.data!!.host}")
            PrintLog.debug("Khaleel", "Intent Data : ${intent.data!!.encodedPath}")
        }

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_feeds,
                R.id.navigation_ebook,
                R.id.navigation_saved,
                R.id.navigation_notifications,
                R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        RxBus.subscribe({
            when (it) {
                is NetWorkMessage -> displayMaterialSnackBar(it.message, it.type, container2)
                is DarkModeChanged -> Handler().post {
                    recreate()
                }
                is ShowSnackBar -> displayMaterialSnackBar(
                    it.message,
                    ConnectivityType.OTHER,
                    container2
                )
            }
        }, {
            PrintLog.debug("Marxist", "$it")
        })
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
