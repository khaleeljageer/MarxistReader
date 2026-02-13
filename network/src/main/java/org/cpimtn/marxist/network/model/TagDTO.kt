package org.cpimtn.marxist.network.model

import kotlinx.serialization.Serializable

@Serializable
data class TagDTO(
    val id: Int,
    val name: String,
)
