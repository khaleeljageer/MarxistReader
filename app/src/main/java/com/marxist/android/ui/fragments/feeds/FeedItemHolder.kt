package com.marxist.android.ui.fragments.feeds

import android.os.Build
import android.text.Html
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import com.marxist.android.R
import com.marxist.android.database.entities.LocalFeeds
import com.marxist.android.ui.base.BaseViewHolder
import kotlinx.android.synthetic.main.feed_item_view.view.*
import java.text.SimpleDateFormat
import java.util.*

class FeedItemHolder(parent: ViewGroup, layoutID: Int) :
    BaseViewHolder<LocalFeeds>(parent, layoutID) {
    override fun bindData(article: LocalFeeds) {
        itemView.txtFeedTitle.text = article.title
        itemView.txtFeedDesc.text = HtmlCompat.fromHtml(article.description, HtmlCompat.FROM_HTML_MODE_LEGACY)

        itemView.txtPubDate.text = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(
            Date(article.pubDate)
        ).toString()

        itemView.ivAudioLogo.visibility = if (article.audioUrl.isEmpty()) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }

        itemView.pbReadPercent.apply {
            visibility = if (article.readPercent == 0) {
                View.INVISIBLE
            } else {
                View.VISIBLE
            }
            progress = article.readPercent
        }
    }
}