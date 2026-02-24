package org.cpimtn.marxist.android.domain.model

/**
 * A month entry for the timeline filter (e.g. "பிப் 2026").
 * [label] is the display label; [key] is the year-month key (e.g. "2026-02") for filtering.
 */
data class TimelineMonth(
    val label: String,
    val key: String,
)
