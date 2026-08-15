package org.cpimtn.marxist.android.domain.model

/**
 * Category with the number of posts in that category (for search/browse UI).
 */
data class CategoryWithCount(
    val category: Category,
    val postCount: Int,
)
