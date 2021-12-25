package com.marxist.android.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.*
import com.marxist.android.R
import com.marxist.android.databinding.ActivitySplashBinding
import com.marxist.android.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class SplashActivity : BaseActivity() {
    private val binding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }
    private var disposable: Disposable? = null
    private var activityDestroyed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.txtLoading.visibility = View.VISIBLE
        binding.progressLoader.visibility = View.VISIBLE

        initTimer()
    }

    private fun initTimer() {
        disposable = Observable.timer(3, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                launchNextActivity()
            }, {
                launchNextActivity()
            })
    }

    private fun launchNextActivity() {
        val mainIntent = Intent(applicationContext, MainActivity::class.java)
        mainIntent.data = intent.data
        startActivity(mainIntent)
        finishAffinity()
    }

    override fun onResume() {
        super.onResume()

        val scaleAnim = ScaleAnimation(
            0f, 1f,
            0f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        val alphaAnim = AlphaAnimation(0f, 1f)
        val animSet = AnimationSet(true)
        animSet.addAnimation(scaleAnim)
        animSet.addAnimation(alphaAnim)

        animSet.interpolator = OvershootInterpolator()
        animSet.duration = 600
        binding.llSplashLogo.startAnimation(animSet)
    }

/*    private var pageIndex = 1
    private var maxPage = 5
    private var allowToContinue = true*/

    /*private fun fetchFeeds() {
        feedDisposable = ApiClient.mApiService.getFeeds(pageIndex)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .retryWhen(RetryWithDelay())
            .doOnComplete {
                if (pageIndex == maxPage) {
                    launchMainActivity()
                } else {
                    pageIndex++
                    if (allowToContinue) {
                        fetchFeeds()
                    } else {
                        launchMainActivity()
                    }
                }
            }
            .subscribe({
                allowToContinue = feedsViewModel.updateFeeds(it)
                if (!allowToContinue) {
                    txtLoading.apply {
                        setTextColor(Color.RED)
                        setTexts(arrayOf("Something went wrong", "Try again later"))
                    }
                }
            }, {
                it.printStackTrace()
                launchMainActivity()
            })
    }*/

/*    private fun launchMainActivity() {
        if (pageIndex != 1) {
            AppPreference.customPrefs(applicationContext)[AppConstants.SharedPreference.PAGED_INDEX] =
                (pageIndex + 1)
        }
        val mainIntent = Intent(applicationContext, MainActivity::class.java)
        mainIntent.data = intent.data
        startActivity(mainIntent)
        finish()
    }*/

    override fun onDestroy() {
        activityDestroyed = true
        super.onDestroy()
        disposable?.dispose()
    }
}