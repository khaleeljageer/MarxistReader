package com.marxist.android.ui.activities

import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.marxist.android.R
import com.marxist.android.databinding.ActivityMainBinding
import com.marxist.android.model.ConnectivityType
import com.marxist.android.model.DarkModeChanged
import com.marxist.android.model.NetWorkMessage
import com.marxist.android.model.ShowSnackBar
import com.marxist.android.ui.base.BaseActivity
import com.marxist.android.utils.DeviceUtils
import com.marxist.android.utils.PrintLog
import com.marxist.android.utils.RxBus
import com.marxist.android.utils.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    companion object {
        const val PLAY_NEW_VIDEO = "com.marxist.android.ui.activities.PLAY_NEW_VIDEO"
    }

    private var currentNavController: LiveData<NavController>? = null

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }

        val path = DeviceUtils.getRootDirPath(baseContext)

        RxBus.subscribe({
            when (it) {
                is NetWorkMessage -> displayMaterialSnackBar(
                    it.message,
                    it.type,
                    binding.container2
                )
                is DarkModeChanged -> Handler().post {
                    recreate()
                }
                is ShowSnackBar -> displayMaterialSnackBar(
                    it.message,
                    ConnectivityType.OTHER,
                    binding.container2
                )
            }
        }, {
            PrintLog.debug("Marxist", "$it")
        })
    }

    private fun setupBottomNavigationBar() {
        val navigationView = findViewById<BottomNavigationView>(R.id.nav_view)

        val navGraphIds = listOf(
            R.navigation.nav_feeds,
            R.navigation.nav_ebooks,
            R.navigation.nav_saved,
            R.navigation.nav_notification,
            R.navigation.nav_settings
        )

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = navigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )

        // Whenever the selected controller changes, setup the action bar.
        controller.observe(this, { navController ->
            setupActionBarWithNavController(navController)
        })
        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }
}
