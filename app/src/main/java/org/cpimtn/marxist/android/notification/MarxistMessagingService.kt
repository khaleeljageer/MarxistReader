package org.cpimtn.marxist.android.notification

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.cpimtn.marxist.android.MainActivity
import org.cpimtn.marxist.android.R

/**
 * Receives FCM pushes for the [NotificationChannels.NEW_BOOK_ADDED] topic.
 *
 * The server publishes to the "new_book_added" topic (subscribed to in [MarxistReaderApp]). We show a
 * notification whose tap action simply launches [MainActivity]; there is no deep-link target yet.
 */
class MarxistMessagingService : FirebaseMessagingService() {

    // No onNewToken/onRegistered override: this app targets the "new_book_added" *topic*, not individual
    // device tokens or FIDs, and topic subscriptions (done in MarxistReaderApp) survive token rotation on
    // their own. onNewToken is deprecated in favour of the FID-based onRegistered anyway, which we don't need.

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // Prefer the notification payload; fall back to data-only messages so the server can send either.
        val title = message.notification?.title
            ?: message.data["title"]
            ?: getString(R.string.notification_new_book_default_title)
        val body = message.notification?.body
            ?: message.data["body"]
            ?: getString(R.string.notification_new_book_default_body)

        showNotification(title, body)
    }

    private fun showNotification(title: String, body: String) {
        // On Android 13+ posting silently fails without the runtime permission; bail early to avoid a crash.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Channel is normally created at app start, but recreate defensively in case the process was
        // spawned directly for this message before MarxistReaderApp ran.
        NotificationChannels.registerAll(this)

        val launchIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            // Tell MainActivity to select the Books tab instead of just resuming wherever it was.
            putExtra(MainActivity.EXTRA_START_TAB, MainActivity.TAB_BOOKS)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(this, NotificationChannels.NEW_BOOK_ADDED)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
    }

    private companion object {
        // Fixed id: a newer "new book" notification replaces any still-showing older one.
        const val NOTIFICATION_ID = 1001
    }
}
