package org.cpimtn.marxist.android.data.source.local.database.entity

/**
 * Lightweight row for list queries (feed, saved). Excludes [content] to avoid loading
 * large text. Use [PostEntity] via getPostById() when full content is needed.
 */
data class PostListRow(
    val id: Int,
    val date: String,
    val slug: String,
    val title: String,
    val excerpt: String,
    val tagsId: List<Int>,
    val categoriesId: List<Int>,
)
