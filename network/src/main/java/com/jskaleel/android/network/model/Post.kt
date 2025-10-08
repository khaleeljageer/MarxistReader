package org.cpimtn.marxist.android.network.model

import com.google.gson.annotations.SerializedName

data class Post(
    val id: Int,
    val date: String,
    val slug: String,
    val title: RenderedObject,
    val excerpt: RenderedObject,
    @SerializedName("tags_names")
    val tags: List<String>,
    @SerializedName("categories_names")
    val categories: List<String>
)

data class RenderedObject(
    val rendered: String
)
