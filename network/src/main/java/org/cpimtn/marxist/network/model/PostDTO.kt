package org.cpimtn.marxist.network.model

import kotlinx.serialization.Serializable


@Serializable
data class PostDTO(
    val id: Int,
    val date: String,
    val slug: String,
    val title: RenderedText,
    val excerpt: RenderedText,
    val content: RenderedText,
    val tags: List<Int>? = null,
    val categories: List<Int>? = null
)

@Serializable
data class RenderedText(
    val rendered: String
)
