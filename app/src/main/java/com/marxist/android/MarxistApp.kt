package com.marxist.android

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.multidex.MultiDex
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.marxist.android.utils.DeviceUtils
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.io.File
import java.util.*

@HiltAndroidApp
class MarxistApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        MultiDex.install(this@MarxistApp)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        initDirPath()
        initFirebase()
        initNotificationChannel()
    }

    private fun initFirebase() {

        FirebaseMessaging.getInstance()
            .subscribeToTopic(getString(R.string.default_notification_channel_id))
        FirebaseMessaging.getInstance()
            .subscribeToTopic(getString(R.string.marxist_instant_news))
    }

    private fun initDirPath() {
        val path = DeviceUtils.getRootDirPath(applicationContext)
        val booksPath = File(path.plus("/books"))
        if (!booksPath.exists()) {
            booksPath.mkdir()
        }
    }

    private fun initNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notificationChannels = ArrayList<NotificationChannel>()

            val pushNotification = NotificationChannel(
                getString(R.string.default_notification_channel_id),
                "General Notification",
                NotificationManager.IMPORTANCE_HIGH
            )

            val instanceNotification = NotificationChannel(
                getString(R.string.instant_news),
                "Instance news",
                NotificationManager.IMPORTANCE_HIGH
            )
            pushNotification.setShowBadge(true)
            instanceNotification.setShowBadge(true)

            notificationChannels.add(pushNotification)
            notificationChannels.add(instanceNotification)

            notificationManager.createNotificationChannels(notificationChannels)
        }
    }
}