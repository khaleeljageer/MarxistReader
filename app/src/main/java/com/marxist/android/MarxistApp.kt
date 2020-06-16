package com.marxist.android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import androidx.multidex.MultiDex
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.marxist.android.database.AppDatabase
import com.marxist.android.di.feedsModule
import com.marxist.android.di.networkModule
import com.marxist.android.di.roomModule
import com.marxist.android.utils.network.NetworkSchedulerService
import org.geometerplus.android.fbreader.FBReaderApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import java.util.*


class MarxistApp : FBReaderApplication(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        MultiDex.install(this@MarxistApp)

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@MarxistApp)
            modules(listOf(networkModule, roomModule, feedsModule))
        }

        FirebaseMessaging.getInstance()
            .subscribeToTopic(getString(R.string.default_notification_channel_id))
        FirebaseMessaging.getInstance()
            .subscribeToTopic(getString(R.string.marxist_instant_news))

        scheduleJob()

        initNotificationChannel()
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .crossfade(true)
            .availableMemoryPercentage(0.3)
            .bitmapPoolPercentage(0.8)
            .build()
    }

    private fun scheduleJob() {
        val myJob = JobInfo.Builder(0, ComponentName(this, NetworkSchedulerService::class.java))
            .setMinimumLatency(1000)
            .setOverrideDeadline(2000)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPersisted(true)
            .build()

        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(myJob)
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