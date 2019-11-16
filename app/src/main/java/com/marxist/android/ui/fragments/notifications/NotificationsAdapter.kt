package com.marxist.android.ui.fragments.bookmark

import android.content.Context
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.marxist.android.R
import com.marxist.android.database.entities.LocalNotifications
import com.marxist.android.ui.base.ItemClickListener
import com.marxist.android.utils.DeviceUtils
import kotlinx.android.synthetic.main.highlight_item_view.view.*

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
        holder.itemView.setOnClickListener {
            showPopupMenu(item, holder)
        }
        holder.itemView.txtMore.setOnClickListener {
            showPopupMenu(item, holder)
        }
    }

    private fun showPopupMenu(
        item: LocalNotifications,
        holder: NotificationItemHolder
    ) {
        val popup = PopupMenu(mContext, holder.itemView.txtMore)
        popup.inflate(R.menu.menu_bookmar_popup)
        popup.setOnMenuItemClickListener {
            if (it != null) {
                when (it.itemId) {
                    R.id.menu_share -> {
                        DeviceUtils.shareIntent(
                            item.title, item.message.plus("\n\n")
                                //.plus(mContext.getString(R.string.to_read_more))
                                .plus(item.message), mContext
                        )
                    }
                    R.id.menu_remove -> {
                        mutableList.removeAt(holder.adapterPosition)
                        itemClickListener.feedItemClickListener(
                            item,
                            holder.adapterPosition,
                            holder.itemView.txtMore
                        )
                    }
                }
            }
            false
        }
        popup.show()
    }

    fun addNotifications(highLights: MutableList<LocalNotifications>) {
        mutableList.addAll(highLights)
        notifyDataSetChanged()
    }
}