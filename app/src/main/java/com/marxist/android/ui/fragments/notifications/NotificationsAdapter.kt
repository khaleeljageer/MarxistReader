package com.marxist.android.ui.fragments.notifications

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.marxist.android.R
import com.marxist.android.model.LocalNotifications
import com.marxist.android.ui.base.ItemClickListener

class NotificationsAdapter(
    private val mContext: Context,
    private val mutableList: MutableList<LocalNotifications>,
    private val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<NotificationItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationItemHolder {
        return NotificationItemHolder(parent, R.layout.highlight_item_view)
    }

    override fun getItemCount() = mutableList.size

    override fun onBindViewHolder(holder: NotificationItemHolder, position: Int) {
        val item = mutableList[holder.adapterPosition]
        holder.bindData(item)
    }

    fun addNotifications(highLights: MutableList<LocalNotifications>) {
        mutableList.addAll(highLights)
        notifyDataSetChanged()
    }
}