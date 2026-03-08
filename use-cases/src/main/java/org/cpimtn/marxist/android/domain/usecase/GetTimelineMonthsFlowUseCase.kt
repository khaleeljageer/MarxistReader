package org.cpimtn.marxist.android.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.cpimtn.marxist.android.domain.model.TimelineMonth

/**
 * Distinct months from post dates for timeline filter (newest first). Uses Tamil month abbreviations for labels.
 */
class GetTimelineMonthsFlowUseCase(
    private val getPostsFlowUseCase: GetPostsFlowUseCase,
) {
    operator fun invoke(maxEntries: Int = 12): Flow<List<TimelineMonth>> =
        getPostsFlowUseCase().map { posts ->
            posts
                .asSequence()
                .mapNotNull { post -> parseYearMonth(post.date) }
                .distinct()
                .sortedWith(compareByDescending<Pair<Int, Int>> { it.first }.thenByDescending { it.second })
                .take(maxEntries)
                .map { (year, month) -> TimelineMonth(label = formatTamilMonthYear(month, year), key = "$year-${month.toString().padStart(2, '0')}") }
                .toList()
        }

    private fun parseYearMonth(dateStr: String): Pair<Int, Int>? {
        return try {
            val part = dateStr.trim().take(7) // "yyyy-MM"
            val parts = part.split("-")
            if (parts.size != 2) return null
            val year = parts[0].toIntOrNull() ?: return null
            val month = parts[1].toIntOrNull() ?: return null
            if (month !in 1..12) return null
            year to month
        } catch (_: Exception) {
            null
        }
    }

    private fun formatTamilMonthYear(month: Int, year: Int): String {
        val abbrev = TAMIL_MONTH_ABBREV[month] ?: month.toString()
        return "$abbrev $year"
    }

    companion object {
        private val TAMIL_MONTH_ABBREV = mapOf(
            1 to "ஜனவரி",
            2 to "பிப்ரவரி",
            3 to "மார்ச்",
            4 to "ஏப்ரல்",
            5 to "மே",
            6 to "ஜூன்",
            7 to "ஜூலை",
            8 to "ஆகஸ்ட்",
            9 to "செப்டம்பர்",
            10 to "அக்டோபர்",
            11 to "நவம்பர்",
            12 to "டிசம்பர்",
        )
    }
}
