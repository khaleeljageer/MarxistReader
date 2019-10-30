package com.marxist.android.ui.base

import android.view.View

interface ItemClickListener {
    fun feedItemClickListener(
        article: Any,
        adapterPosition: Int,
        view: View
    )
}