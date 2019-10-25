package com.marxist.android.ui.activities

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.widget.NestedScrollView
import com.google.android.material.appbar.AppBarLayout
import com.marxist.android.R
import com.marxist.android.database.entities.LocalFeeds
import com.marxist.android.ui.base.BaseActivity
import com.marxist.android.utils.AppPreference.get
import kotlinx.android.synthetic.main.activity_details.*
import kotlin.math.roundToInt

class DetailsActivity : BaseActivity() {
    private var article: LocalFeeds? = null
    private var readPercent: Int = 0
    private var isEndReached: Boolean = false

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
        txtContent.apply {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
            setHtml(article!!.content)
        }

        txtContent.setHtml(article!!.content)
        article!!.isBookMarked.apply {
            ivBookMark.tag = this
            if (this) {
                ivBookMark.playAnimation()
            }
        }

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

        nestedScroll.setOnScrollChangeListener { scrollView: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (scrollView != null) {
                val totalHeight =
                    scrollView.getChildAt(0).measuredHeight - scrollView.measuredHeight
                isEndReached = scrollY == totalHeight
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
    }

    override fun onDestroy() {
        super.onDestroy()
        System.gc()
    }
}