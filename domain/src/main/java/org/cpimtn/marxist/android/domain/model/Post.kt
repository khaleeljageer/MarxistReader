package org.cpimtn.marxist.android.domain.model

import java.text.SimpleDateFormat
import java.util.Locale

data class Post(
    val id: Int,
    val date: String,
    val slug: String,
    val title: String,
    val excerpt: String,
    val tags: List<Int>,
    val categories: List<Int>
) {
    // In your Post data class or a mapper
    val formattedDate: String
        get() = try {
            val iso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)
            val out = SimpleDateFormat("MMM d", Locale.getDefault())
            out.format(iso.parse(date.trim().take(19))!!)
        } catch (_: Exception) {
            date.take(10)
        }
}
