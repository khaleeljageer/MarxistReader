package com.marxist.android.utils

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class GridItemSpace(
    private val context: Context,
    private val spacing: Int
) : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.layoutManager is GridLayoutManager) {
            val spanCount = (parent.layoutManager as GridLayoutManager).spanCount
            val position: Int = parent.getChildAdapterPosition(view)
            val column = position % spanCount
            outRect.left =
                spacing - column * spacing / spanCount
            outRect.right =
                (column + 1) * spacing / spanCount
            if (position < spanCount) {
                outRect.top = 10.toPixel(context)
            }
            outRect.bottom = 10.toPixel(context)
        }
    }
}

fun Int.toPixel(context: Context): Int = (this * context.resources.displayMetrics.density).toInt()