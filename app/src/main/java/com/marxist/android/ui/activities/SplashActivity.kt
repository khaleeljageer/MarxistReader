package com.marxist.android.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.*
import com.marxist.android.R
import com.marxist.android.database.entities.LocalFeeds
import com.marxist.android.ui.base.BaseActivity
import com.marxist.android.utils.api.ApiClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_splash.*
import java.text.SimpleDateFormat
import java.util.*


class SplashActivity : BaseActivity() {
    private var feedDisposable: Disposable? = null
    private var activityDestroyed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        txtLoading.visibility = View.VISIBLE
        progressLoader.visibility = View.VISIBLE

        val allFeeds = appDatabase.localFeedsDao().getAllFeeds()
        maxPage = if (allFeeds.isNotEmpty()) {
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
            .retry(3)
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
                allowToContinue = if (it?.channel != null && it.channel!!.itemList != null) {
                    val itemList = it.channel!!.itemList
                    if (itemList != null && itemList.isNotEmpty()) {
                        itemList.forEach { feed ->
                            val simpleDateFormat =
                                SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
                            val mDate = simpleDateFormat.parse(feed.pubDate)
                            val timeInMillis = mDate.time

                            val localFeeds = LocalFeeds(
                                feed.title!!,
                                feed.link!!,
                                timeInMillis,
                                feed.description!!,
                                feed.content!!,
                                if (feed.enclosure == null) {
                                    ""
                                } else {
                                    feed.enclosure!!.audioUrl!!
                                },
                                isDownloaded = false,
                                isBookMarked = false, readPercent = 0
                            )
                            appDatabase.localFeedsDao().insert(localFeeds)
                        }
                    }
                    true
                } else {
                    txtLoading.apply {
                        setTextColor(Color.RED)
                        setTexts(arrayOf("Something went wrong", "Try again later"))
                    }
                    false
                }
            }, {
                it.printStackTrace()
                launchMainActivity()
            })
    }

    private fun launchMainActivity() {
        startActivity(Intent(applicationContext, MainActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        activityDestroyed = true
        super.onDestroy()
        feedDisposable?.dispose()
    }
}