package org.cpimtn.marxist.android.data

interface FeedRepository {
    // A suspend function to get the feed items.
    // This will be implemented to fetch data from the network or a local database.
    suspend fun getFeedItems(): List<String>
}
