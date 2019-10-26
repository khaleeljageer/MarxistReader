package com.marxist.android.ui.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.widget.NestedScrollView
import com.google.android.material.appbar.AppBarLayout
import com.marxist.android.R
import com.marxist.android.database.entities.LocalFeeds
import com.marxist.android.ui.base.BaseActivity
import com.marxist.android.utils.AppPreference.get
import com.marxist.android.utils.PrintLog
import kotlinx.android.synthetic.main.activity_details.*
import kotlin.math.roundToInt

class DetailsActivity : BaseActivity() {
    private var clipManager: ClipboardManager? = null
    private var article: LocalFeeds? = null
    private var readPercent: Int = 0

    companion object {
        const val ARTICLE = "article"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        article = intent.getSerializableExtra(ARTICLE) as LocalFeeds

        article!!.title.apply {
            txtCollapseTitle.text = this
            txtFeedTitle.text = this
        }

        val fontSize = appPreference[getString(R.string.pref_key_font_size), 14]

        if (article!!.audioUrl.isEmpty()) {
            fabAudio.visibility = View.INVISIBLE
        }

        var content = article!!.content
        try {
            val regex1 = Regex("(?s)<div class=\"wpcnt\">.*?</div>")
            content = content.replace(regex1, "")
            val regex2 = Regex("(?s)<script type=\"text/javascript\">.*?</script>")
            content = content.replace(regex2, "")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            txtContent.apply {
                setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
                setHtml(content)
            }
        }

        article!!.isBookMarked.apply {
            ivBookMark.tag = this
            if (this) {
                ivBookMark.playAnimation()
            }
        }


        clipManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipManager!!.addPrimaryClipChangedListener(object :
            ClipboardManager.OnPrimaryClipChangedListener {
            override fun onPrimaryClipChanged() {
                clipManager!!.removePrimaryClipChangedListener(this)
                val primaryCLip = clipManager!!.primaryClip
                if (primaryCLip != null) {
                    val clip = primaryCLip.getItemAt(0).text
                    PrintLog.debug("Khaleel", "Clip : $clip")
                    clipManager!!.primaryClip =
                        ClipData.newPlainText(
                            "marxist_clip",
                            clip.toString().plus("\n படிக்க : ${article!!.link}")
                        )
                }

                clipManager!!.addPrimaryClipChangedListener(this)
            }
        })

        initListeners()
    }

    private fun initListeners() {
        ivBookMark.setOnClickListener {
            val tag = it.tag as Boolean
            if (tag) {
                ivBookMark.playAnimation().apply {
                    ivBookMark.setAnimation(R.raw.bookmark_anim)
                }
            } else {
                ivBookMark.playAnimation()
            }

            ivBookMark.tag = !tag
            article!!.isBookMarked = !tag
            appDatabase.localFeedsDao()
                .updateBookMarkStatus(!tag, article!!.title, article!!.pubDate)
        }

        appBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var scrollRange = -1

            override fun onOffsetChanged(appBar: AppBarLayout?, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBar!!.totalScrollRange
                }
                txtFeedTitle.visibility = if (scrollRange + verticalOffset == 0) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }
            }
        })

        nestedScroll.setOnScrollChangeListener { scrollView: NestedScrollView?, _: Int, scrollY: Int, _: Int, _: Int ->
            if (scrollView != null) {
                val totalHeight =
                    scrollView.getChildAt(0).measuredHeight - scrollView.measuredHeight
//                isEndReached = scrollY == totalHeight
                readPercent = (scrollY.toDouble() / totalHeight.toDouble() * 100).roundToInt()
            }
        }

        ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        if (article != null) {
            appDatabase.localFeedsDao()
                .updateReadPercentage(readPercent, article!!.title, article!!.pubDate)
        }
        super.onBackPressed()
        clipManager!!.removePrimaryClipChangedListener(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        System.gc()
    }
}