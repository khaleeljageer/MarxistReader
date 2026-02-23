package org.cpimtn.marxist.android.domain.util

import kotlin.math.ceil

/**
 * Estimates reading time for Tamil content.
 *
 * Tamil reading speed factors:
 *  - Average Tamil reader: ~150 words/min (vs ~230 for English)
 *  - Tamil words are longer (avg 4-5 syllables vs 1-2 for English)
 *  - Mixed Tamil+English content reads slightly faster
 *
 * We use word count, not character count, because Tamil Unicode
 * characters don't map 1:1 to visual complexity (combining marks,
 * conjuncts like க்ஷ count as multiple chars but read as one unit).
 */
object ReadTimeEstimator {

    private const val TAMIL_WORDS_PER_MINUTE = 150
    private const val ENGLISH_WORDS_PER_MINUTE = 230
    private const val MINIMUM_READ_TIME_MINUTES = 1

    // Tamil Unicode range: U+0B80–U+0BFF
    private val TAMIL_CHAR_REGEX = Regex("[\u0B80-\u0BFF]")
    private val HTML_TAG_REGEX = Regex("<[^>]+>")
    private val WHITESPACE_REGEX = Regex("\\s+")

    fun estimate(htmlContent: String): Int {
        // Strip HTML tags
        val plainText = htmlContent
            .replace(HTML_TAG_REGEX, " ")
            .trim()

        if (plainText.isBlank()) return MINIMUM_READ_TIME_MINUTES

        val words = plainText.split(WHITESPACE_REGEX).filter { it.isNotBlank() }
        val totalWords = words.size

        if (totalWords == 0) return MINIMUM_READ_TIME_MINUTES

        // Calculate Tamil vs English ratio
        val tamilWordCount = words.count { word ->
            TAMIL_CHAR_REGEX.containsMatchIn(word)
        }
        val englishWordCount = totalWords - tamilWordCount

        // Weighted reading time
        val tamilMinutes = tamilWordCount.toFloat() / TAMIL_WORDS_PER_MINUTE
        val englishMinutes = englishWordCount.toFloat() / ENGLISH_WORDS_PER_MINUTE
        val totalMinutes = tamilMinutes + englishMinutes

        return maxOf(MINIMUM_READ_TIME_MINUTES, ceil(totalMinutes.toDouble()).toInt())
    }

    /**
     * Formatted string for UI display.
     * Returns: "5 நிமி" or "1 நிமி"
     */
    fun formatTamil(htmlContent: String): String {
        return "${estimate(htmlContent)} நிமி"
    }
}