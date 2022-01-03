package com.marxist.android.ui.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.marxist.android.databinding.ListFeedsBottomProgressBinding

internal class ProgressViewHolder(private val parent: ListFeedsBottomProgressBinding) :
    RecyclerView.ViewHolder(parent.root) {
    fun bindData(item: Any) {
        parent.rlFeedProgress.visibility = View.VISIBLE
        parent.rlFeedProgress.isClickable = false
    }
}