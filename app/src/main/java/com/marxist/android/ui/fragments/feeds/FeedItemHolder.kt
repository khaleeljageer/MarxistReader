package com.marxist.android.ui.fragments.feeds

import android.content.Context
import android.view.View
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.marxist.android.R
import com.marxist.android.data.model.WPPost
import com.marxist.android.databinding.FeedItemViewBinding
import com.marxist.android.utils.DeviceUtils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor
import kotlin.math.roundToInt


class FeedItemHolder(private val context: Context, private val parent: FeedItemViewBinding) :
    RecyclerView.ViewHolder(parent.root) {
    fun bindData(item: WPPost) {
        parent.txtFeedTitle.text =
            HtmlCompat.fromHtml(item.title.rendered, HtmlCompat.FROM_HTML_MODE_COMPACT)

        val wordCount = item.content.rendered.wordCount()
        val estimated = wordCount.estimateTime()
        parent.txtPubDate.text = formatDate(item.date)
        parent.txtEstimate.text =
            context.getString(R.string.estimate).plus(estimated)
        parent.rootCard.setCardBackgroundColor(DeviceUtils.getColor(context))

        parent.ivAudioLogo.visibility = if (item.audioUrl.isNullOrEmpty()) {
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

    private fun formatDate(pubDate: String): String {
        val wpDateFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(pubDate)

        return try {
            val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
            sdf.format(wpDateFormat)
        } catch (e: Exception) {
            e.printStackTrace()
            pubDate
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