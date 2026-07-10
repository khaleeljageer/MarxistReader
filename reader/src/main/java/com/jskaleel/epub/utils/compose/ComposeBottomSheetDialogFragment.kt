/*
 * Copyright 2022 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package com.jskaleel.epub.utils.compose

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.widget.NestedScrollView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.R as MaterialR
import androidx.core.graphics.drawable.toDrawable

/**
 * A bottom sheet whose content is built using Jetpack Compose.
 */
abstract class ComposeBottomSheetDialogFragment(
    private val isScrollable: Boolean = false,
) : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        super.onCreateDialog(savedInstanceState).apply {
            // The Compose Surface below draws the sheet's own rounded background, so the
            // dialog window's square background must be transparent or it'd show through
            // (and square off) the rounded top corners.
            window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }

    override fun onStart() {
        super.onStart()
        // BottomSheetDialog wraps our content in its own opaque FrameLayout
        // (design_bottom_sheet) with a square white background — that view sits behind the
        // Compose Surface and isn't clipped by its rounded shape, so it must be made
        // transparent too or it shows through as square corners.
        dialog?.findViewById<View>(MaterialR.id.design_bottom_sheet)
            ?.background = Color.TRANSPARENT.toDrawable()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val context = requireContext()
        val composeView = ComposeView(context).apply {
            setContent {
                AppTheme {
                    Surface(shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
                        this@ComposeBottomSheetDialogFragment.Content()
                    }
                }
            }
        }

        return if (isScrollable) {
            NestedScrollView(context).apply {
                addView(
                    composeView,
                    FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
            }
        } else {
            composeView
        }
    }

    @Composable
    abstract fun Content()
}
