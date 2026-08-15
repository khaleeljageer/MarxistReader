package org.cpimtn.marxist.android.domain.model

data class Book(
    val id: String,
    val title: String,
    val date: String,
    val imageUrl: String,
    val epubUrl: String,
) {
    /** Best-effort 4-digit year extracted from [date] (e.g. "Jan, 2022" -> "2022"), for display. */
    val year: String
        get() = YEAR_REGEX.find(date)?.value ?: date

    private companion object {
        val YEAR_REGEX = Regex("\\d{4}")
    }
}
