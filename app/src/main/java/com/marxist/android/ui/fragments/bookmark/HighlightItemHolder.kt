package com.marxist.android.ui.fragments.bookmark

import android.view.ViewGroup
import com.marxist.android.R
import com.marxist.android.database.entities.LocalHighlights
import com.marxist.android.ui.base.BaseViewHolder
import com.marxist.android.utils.DeviceUtils
import kotlinx.android.synthetic.main.highlight_item_view.view.*

class HighlightItemHolder(private val parent: ViewGroup, layoutID: Int) :
    BaseViewHolder<LocalHighlights>(parent, layoutID) {
    override fun bindData(item: LocalHighlights) {
        itemView.txtFeedTitle.text = item.title
        itemView.txtHighLight.text = item.highlight
    }
}