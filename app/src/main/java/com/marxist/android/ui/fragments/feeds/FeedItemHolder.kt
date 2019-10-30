package com.marxist.android.ui.fragments.feeds

import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import com.marxist.android.database.entities.LocalFeeds
import com.marxist.android.ui.base.BaseViewHolder
import com.marxist.android.utils.PrintLog
import kotlinx.android.synthetic.main.feed_item_view.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor
import kotlin.math.roundToInt


class FeedItemHolder(private val parent: ViewGroup, layoutID: Int) :
    BaseViewHolder<LocalFeeds>(parent, layoutID) {
    override fun bindData(item: LocalFeeds) {
        itemView.txtFeedTitle.text = item.title
        itemView.txtFeedDesc.text =
            HtmlCompat.fromHtml(item.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
        val pubDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(
            Date(item.pubDate)
        ).toString()
        val wordCount = item.content.wordCount()
        val estimated = wordCount.estimateTime()

        itemView.txtPubDate.text =
            pubDate.plus(parent.context.getString(com.marxist.android.R.string.estimate))
                .plus(estimated)

        itemView.ivAudioLogo.visibility = if (item.audioUrl.isEmpty()) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }

    private fun Int.estimateTime(): String {
        val count = this
        return if (count < 200) {
            val dec = (count.div(200.toDouble()) - floor(count.div(200.toDouble()))).times(60)
            "${dec.roundToInt()} sec read"
        } else {
            val hour = count.div(200)
            if (hour > 60) {
                "1 hour read"
            } else {
                val min = floor(count.div(200f)).times(0.60)
                if (min > 1) {
                    hour.inc()
                }
                "$hour min read"
            }
        }
    }

    private fun String.wordCount(): Int {
        val text = this
        return text.replace("\n", "").split(" ").size
        /*
        var countWords = 0
        for (i in 0 until text.length - 1) {
            if (text[i] == ' ' && text[i + 1] != ' ')
                countWords++
        }
        return countWords + 1*/
    }
}