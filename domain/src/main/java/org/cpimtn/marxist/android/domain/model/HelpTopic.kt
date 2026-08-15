package org.cpimtn.marxist.android.domain.model

/**
 * Identifies a screen that has its own contextual "how to use" help.
 * The [name] doubles as the persisted key in the "seen help topics" set,
 * so renaming an entry resets its first-run auto-show for existing users.
 */
enum class HelpTopic {
    FEED,
    BOOKS,
    SEARCH,
    SAVED,
    SETTINGS,
    ARTICLE,
}
