package com.marxist.android.ui.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseBookViewHolder<T>(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bindData(book: T, position: Int)
}