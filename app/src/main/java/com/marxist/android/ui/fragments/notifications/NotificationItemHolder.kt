package com.marxist.android.ui.fragments.notifications

import android.view.ViewGroup
import com.marxist.android.model.LocalNotifications
import com.marxist.android.ui.base.BaseViewHolder
import kotlinx.android.synthetic.main.highlight_item_view.view.*

class NotificationItemHolder(private val parent: ViewGroup, layoutID: Int) :
    BaseViewHolder<LocalNotifications>(parent, layoutID) {
    override fun bindData(item: LocalNotifications) {
        itemView.txtFeedTitle.text = item.title
        itemView.txtHighLight.text = item.message
    }
}