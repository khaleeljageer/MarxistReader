package com.marxist.android.ui.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationBarView
import com.marxist.android.R
import com.marxist.android.databinding.ActivityMainBinding
import com.marxist.android.model.ShowSnackBar
import com.marxist.android.ui.activities.details.DetailsViewModel
import com.marxist.android.ui.base.BaseActivity
import com.marxist.android.ui.base.FragmentsAdapter
import com.marxist.android.utils.EventBus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val detailsViewModel: DetailsViewModel by viewModels()

    @Inject
    lateinit var eventBus: EventBus

    private val fragmentsAdapter by lazy {
        FragmentsAdapter(this@MainActivity, listOf(0, 1, 2, 3))
    }

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
        detailsViewModel.triggerArticleLink("https://marxistreader.home.blog")

        lifecycleScope.launch {
            eventBus.events.collect {
                when (it) {
                    is ShowSnackBar -> displayMaterialSnackBar(
                        it.message,
                        binding.container2
                    )
                }
            }
        }

        with(binding.viewPager) {
            this.offscreenPageLimit = 3
            this.adapter = fragmentsAdapter
            this.isUserInputEnabled = false
        }

        updateTitle(0)
    }

    private fun setupBottomNavigationBar() {
        binding.navView.setOnItemSelectedListener(onItemSelectedListener)
    }

    private val onItemSelectedListener = NavigationBarView.OnItemSelectedListener {
        when (it.itemId) {
            R.id.nav_feeds -> {
                binding.viewPager.setCurrentItem(0, false)
                updateTitle(0)
                return@OnItemSelectedListener true
            }
            R.id.nav_ebooks -> {
                binding.viewPager.setCurrentItem(1, false)
                updateTitle(1)
                return@OnItemSelectedListener true
            }
            R.id.nav_quotes -> {
                binding.viewPager.setCurrentItem(2, false)
                updateTitle(2)
                return@OnItemSelectedListener true
            }
            R.id.nav_settings -> {
                binding.viewPager.setCurrentItem(3, false)
                updateTitle(3)
                return@OnItemSelectedListener true
            }
        }
        return@OnItemSelectedListener false
    }

    private fun updateTitle(index: Int) {
        val title = when (index) {
            1 -> "E-Books"
            2 -> "Quotes"
            3 -> "Settings"
            else -> "Marxist Reader"
        }
        supportActionBar?.title = title
    }
}
