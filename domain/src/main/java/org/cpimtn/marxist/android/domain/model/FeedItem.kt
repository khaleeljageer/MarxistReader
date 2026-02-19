package org.cpimtn.marxist.android.domain.model

import org.cpimtn.marxist.android.domain.util.ReadTimeEstimator

/**
 * Display model for a post in the feed: post data plus resolved category and tag labels
 * for UI (one category, up to two tags). Resolution from IDs to names is done in the use case.
 */
data class FeedItem(
    val post: Post,
    /** Resolved name for the first category, or empty if none. */
    val categoryLabel: String,
    /** Resolved names for up to the first two tags. */
    val tagLabels: List<String>,
    val readTime: String = ReadTimeEstimator.formatTamil(post.excerpt)
)
