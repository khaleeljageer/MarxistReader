package com.marxist.android.ui.fragments.notifications

import androidx.recyclerview.widget.RecyclerView
import com.marxist.android.databinding.HighlightItemViewBinding
import com.marxist.android.model.LocalNotifications

class NotificationItemHolder(private val parent: HighlightItemViewBinding) :
    RecyclerView.ViewHolder(parent.root) {
    fun bindData(item: LocalNotifications) {
        parent.txtFeedTitle.text = item.title
        parent.txtHighLight.text = item.message
    }
}