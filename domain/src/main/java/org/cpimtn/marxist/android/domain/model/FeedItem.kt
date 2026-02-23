package org.cpimtn.marxist.android.domain.model

import org.cpimtn.marxist.android.domain.util.ReadTimeEstimator

/**
 * Display model for a post in the feed: post data plus resolved category and tag labels
 * for UI (one category; tag count is use-case–dependent). Resolution from IDs to names is done in the use case.
 */
data class FeedItem(
    val post: Post,
    /** Resolved name for the first category, or empty if none. */
    val categoryLabel: String,
    /** Resolved tag names for display. List use cases (feed, saved) pass at most 3; single-item use case passes all. */
    val tagLabels: List<String>,
    val readTime: String = ReadTimeEstimator.formatTamil(post.content)
)
