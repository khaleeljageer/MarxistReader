/*
 * Copyright 2022 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package com.jskaleel.epub.reader.preferences

import android.app.Dialog
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jskaleel.epub.reader.ReaderViewModel
import com.jskaleel.epub.utils.compose.ComposeBottomSheetDialogFragment

abstract class UserPreferencesBottomSheetDialogFragment(
    private val title: String,
) : ComposeBottomSheetDialogFragment(
    isScrollable = true
) {
    abstract val preferencesModel: UserPreferencesViewModel<*, *>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
            // Reduce the dim to see the impact of the settings on the page.
            window?.setDimAmount(0.1f)

            behavior.apply {
                // Peek at a partial height, but let the user drag it up to fully expand
                // (wrapping content, capped at screen height) instead of pinning the sheet
                // to a fixed size that only scrolls internally.
                isFitToContents = true
                skipCollapsed = false
                peekHeight = (resources.displayMetrics.heightPixels * 0.6f).toInt()
                state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

    @Composable
    override fun Content() {
        UserPreferences(preferencesModel, title, onDismiss = { dismiss() })
    }
}

class MainPreferencesBottomSheetDialogFragment : UserPreferencesBottomSheetDialogFragment(
    "காட்சி அமைப்பு"
) {

    private val viewModel: ReaderViewModel by activityViewModels()

    override val preferencesModel: UserPreferencesViewModel<*, *> by lazy {
        checkNotNull(viewModel.settings)
    }
}
