package com.marxist.android.ui.fragments.notifications

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.marxist.android.databinding.HighlightItemViewBinding
import com.marxist.android.model.LocalNotifications
import com.marxist.android.ui.base.ItemClickListener

class NotificationsAdapter(
    private val mContext: Context,
    private val mutableList: MutableList<LocalNotifications>,
    private val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<NotificationItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationItemHolder {
        val binding = HighlightItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationItemHolder(binding)
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

    fun updateNotifications(highLights: MutableList<LocalNotifications>) {
        mutableList.clear()
        mutableList.addAll(highLights)
        notifyDataSetChanged()
    }
}