package org.cpimtn.marxist.android.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

/**
 * Subscribes this install to the "new_book_added" FCM topic so the backend can broadcast newly
 * published e-books without tracking individual device tokens. Subscription is persisted by
 * Play Services and is idempotent, so calling this on every cold start is safe and cheap.
 */
fun subscribeToNewBookTopic() {
    FirebaseMessaging.getInstance()
        .subscribeToTopic(NotificationChannels.NEW_BOOK_ADDED)
        .addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FcmTopics", "Failed to subscribe to ${NotificationChannels.NEW_BOOK_ADDED} topic", task.exception)
            }
        }
}
