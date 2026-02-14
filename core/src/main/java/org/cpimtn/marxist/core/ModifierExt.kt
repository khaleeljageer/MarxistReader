package org.cpimtn.marxist.core

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier

/**
 * Applies [clickable] only when [condition] is true; otherwise returns this modifier unchanged.
 * Use when a row/card should be clickable only in certain states (e.g. enabled vs disabled).
 */
fun Modifier.clickableIf(
    condition: Boolean,
    onClick: () -> Unit,
): Modifier = if (condition) {
    this.clickable(onClick = onClick)
} else {
    this
}
