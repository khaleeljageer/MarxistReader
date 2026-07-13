package org.cpimtn.marxist.android.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.content.getSystemService
import org.cpimtn.marxist.android.R

/**
 * Centralised registration of the app's notification channels.
 *
 * Channels are idempotent — re-creating an existing channel is a no-op — so this is safe to call on
 * every cold start. It runs unconditionally: the channel must exist before any [POST_NOTIFICATIONS]
 * permission is granted, otherwise the first FCM message would fall back to the miscellaneous channel.
 */
object NotificationChannels {

    /** Channel for "a new e-book was published" push notifications. Also used as the FCM topic name. */
    const val NEW_BOOK_ADDED = "new_book_added"

    fun registerAll(context: Context) {
        val manager = context.getSystemService<NotificationManager>() ?: return

        val newBookAdded = NotificationChannel(
            NEW_BOOK_ADDED,
            context.getString(R.string.notification_channel_new_book_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = context.getString(R.string.notification_channel_new_book_desc)
        }

        manager.createNotificationChannel(newBookAdded)
    }
}
