package com.marxist.android.utils

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.marxist.android.database.AppDatabase
import com.marxist.android.database.entities.LocalNotifications

class NotificationListener : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        try {
            PrintLog.debug("TAG", "FCM MESSAGE FROM ${remoteMessage.from}")
            PrintLog.debug("TAG", "FCM MESSAGE DATA ${remoteMessage.data}")

            if (remoteMessage.data["title"] != null && remoteMessage.data["body"] != null) {
                val title: String = remoteMessage.data["title"].toString()
                val body: String = remoteMessage.data["body"].toString()
                AppDatabase.getAppDatabase(applicationContext).localNotificationsDao().insert(
                    LocalNotifications(
                        System.currentTimeMillis(),
                        title,
                        body
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}