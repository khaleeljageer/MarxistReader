package com.marxist.android

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import com.marxist.android.database.AppDatabase
import com.marxist.android.utils.api.ApiClient
import com.marxist.android.utils.network.NetworkSchedulerService
import java.util.*


class MarxistApp : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseMessaging.getInstance()
            .subscribeToTopic(getString(R.string.default_notification_channel_id))
        FirebaseMessaging.getInstance()
            .subscribeToTopic(getString(R.string.marxist_instant_news))

        AppDatabase.getAppDatabase(applicationContext)
        scheduleJob()
        ApiClient.setApiService()

        initNotificationChannel()
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