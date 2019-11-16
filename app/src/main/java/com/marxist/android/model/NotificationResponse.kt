package com.marxist.android.model

import com.marxist.android.database.entities.LocalNotifications

data class NotificationResponse(val notifications: MutableList<LocalNotifications>)