package org.cpimtn.marxist.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class PostDTO(
    val id: Int,
    val date: String,
    val slug: String,
    val title: RenderedText,
    val excerpt: RenderedText,
    @SerialName("tags_names")
    val tags: List<String>,
    @SerialName("categories_names")
    val categories: List<String>
)

@Serializable
data class RenderedText(
    val rendered: String
)
