package com.marxist.android.ui.base

import android.view.View
import com.marxist.android.database.entities.LocalFeeds

interface FeedItemClickListener {
    fun feedItemClickListener(
        article: LocalFeeds,
        adapterPosition: Int,
        view: View
    )
}