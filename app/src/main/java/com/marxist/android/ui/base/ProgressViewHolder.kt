package com.marxist.android.ui.base

import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_feeds_bottom_progress.view.*

internal class ProgressViewHolder(parent: ViewGroup, layoutID: Int) :
    BaseViewHolder<Any>(parent, layoutID) {
    override fun bindData(item: Any) {
        itemView.rlFeedProgress.visibility = View.VISIBLE
        itemView.rlFeedProgress.isClickable = false
    }
}