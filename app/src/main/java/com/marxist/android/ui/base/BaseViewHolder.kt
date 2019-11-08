package com.marxist.android.ui.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<T>(parent: ViewGroup, layoutID: Int) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(layoutID, parent, false)
) {
    abstract fun bindData(item: T)
}
