package com.marxist.android.ui.activities.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver.OnPreDrawListener
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.marxist.android.databinding.ActivitySplashBinding
import com.marxist.android.ui.activities.MainActivity
import com.marxist.android.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LauncherActivity : BaseActivity() {
    private val binding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }

    private val launcherViewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(binding.root)

        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (launcherViewModel.isReady.value == true) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        initTimer()
                        true
                    } else {
                        false
                    }
                }
            }
        )

        launcherViewModel.delaySplashScreen()
    }

    private fun initTimer() {
        lifecycleScope.launch {
            delay(2000)
            launchNextActivity()
        }
    }

    private fun launchNextActivity() {
        val mainIntent = Intent(applicationContext, MainActivity::class.java)
        mainIntent.data = intent.data
        startActivity(mainIntent)
        finishAffinity()
    }
}