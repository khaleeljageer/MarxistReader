package com.marxist.android.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.*
import androidx.lifecycle.ViewModelProviders
import com.marxist.android.R
import com.marxist.android.ui.base.BaseActivity
import com.marxist.android.utils.api.ApiClient
import com.marxist.android.utils.api.RetryWithDelay
import com.marxist.android.viewmodel.FeedsViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : BaseActivity() {
    private lateinit var feedsViewModel: FeedsViewModel
    private var feedDisposable: Disposable? = null
    private var activityDestroyed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        feedsViewModel = ViewModelProviders.of(this).get(FeedsViewModel::class.java)

        txtLoading.visibility = View.VISIBLE
        progressLoader.visibility = View.VISIBLE

        val allFeeds = feedsViewModel.getFeedsCount()

        maxPage = if (allFeeds > 0) {
            1
        } else {
            10
        }
        fetchFeeds()
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
        llSplashLogo.startAnimation(animSet)
    }

    private var pageIndex = 1
    private var maxPage = 10
    private var allowToContinue = true

    private fun fetchFeeds() {
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
    }

    private fun launchMainActivity() {
        val mainIntent = Intent(applicationContext, MainActivity::class.java)
        mainIntent.data = intent.data
        startActivity(mainIntent)
        finish()
    }

    override fun onDestroy() {
        activityDestroyed = true
        super.onDestroy()
        feedDisposable?.dispose()
    }
}