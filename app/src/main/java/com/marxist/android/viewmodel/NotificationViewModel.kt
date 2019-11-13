package com.marxist.android.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.marxist.android.database.AppDatabase
import com.marxist.android.database.dao.LocalHighlightsDao
import com.marxist.android.database.dao.LocalNotificationsDao
import com.marxist.android.database.entities.LocalHighlights
import com.marxist.android.database.entities.LocalNotifications

class NotificationViewModel(application: Application) : AndroidViewModel(application) {
    private var notificationsDao: LocalNotificationsDao =
        AppDatabase.getAppDatabase(application).localNotificationsDao()

    private var notificationLiveData: LiveData<MutableList<LocalNotifications>>

    init {
        notificationLiveData = notificationsDao.getAllNotifications()
    }

    fun insert(highlights: LocalNotifications) {
        notificationsDao.insert(highlights)
    }

    fun getNotifications(): LiveData<MutableList<LocalNotifications>> {
        return notificationLiveData
    }

    fun deleteNotification(notifications: LocalNotifications) {
        notificationsDao.deleteNotification(notifications)
    }
}