package com.marxist.android.ui.activities

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.marxist.android.R
import com.marxist.android.databinding.ActivityMainBinding
import com.marxist.android.model.ConnectivityType
import com.marxist.android.model.ShowSnackBar
import com.marxist.android.ui.base.BaseActivity
import com.marxist.android.utils.EventBus
import com.marxist.android.utils.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var eventBus: EventBus

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

        lifecycleScope.launch {
            eventBus.events.collect {
                when (it) {
                    is ShowSnackBar -> displayMaterialSnackBar(
                        it.message,
                        ConnectivityType.OTHER,
                        binding.container2
                    )
                }
            }
        }
    }

    private fun setupBottomNavigationBar() {
        val navigationView = findViewById<BottomNavigationView>(R.id.nav_view)

        val navGraphIds = listOf(
            R.navigation.nav_feeds,
            R.navigation.nav_ebooks,
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
