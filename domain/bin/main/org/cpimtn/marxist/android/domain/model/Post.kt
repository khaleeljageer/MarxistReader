package org.cpimtn.marxist.android.domain.model

data class Post(
    val id: Int,
    val date: String,
    val slug: String,
    val title: String,
    val excerpt: String,
    val tags: List<String>,
    val categories: List<String>
)
