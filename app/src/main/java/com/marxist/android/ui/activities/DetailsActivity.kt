package com.marxist.android.ui.activities

import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.widget.NestedScrollView
import com.google.android.material.appbar.AppBarLayout
import com.marxist.android.R
import com.marxist.android.database.entities.LocalFeeds
import com.marxist.android.model.ConnectivityType
import com.marxist.android.model.ShareSnackBar
import com.marxist.android.model.ShowSnackBar
import com.marxist.android.ui.base.BaseActivity
import com.marxist.android.ui.fragments.player.AudioPlayerFragment
import com.marxist.android.utils.AppPreference.get
import com.marxist.android.utils.DeviceUtils
import com.marxist.android.utils.RxBus
import kotlinx.android.synthetic.main.activity_details.*
import kotlin.math.roundToInt


class DetailsActivity : BaseActivity() {
    private var mActionMode: ActionMode? = null
    private var article: LocalFeeds? = null
    private var readPercent: Int = 0

    companion object {
        const val ARTICLE = "article"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.menu_share) {
            DeviceUtils.shareIntent(article!!.title, article!!.link, applicationContext)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        setSupportActionBar(toolbar)
        article = intent.getSerializableExtra(ARTICLE) as LocalFeeds

        article!!.title.apply {
            txtCollapseTitle.text = this
            txtFeedTitle.text = this
        }

        val fontSize = appPreference[getString(R.string.pref_key_font_size), 14]

        val type = 1
        if (article!!.audioUrl.isNotEmpty()) {
            cvPlayerView.visibility = View.VISIBLE
            Handler().post {
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.animator.slide_in_from_bottom, R.animator.slide_out_to_bottom,
                        R.animator.slide_in_from_bottom, R.animator.slide_out_to_bottom
                    )
                    .replace(
                        flAudioPlayer.id,
                        AudioPlayerFragment.newInstance(article!!, type)
                    )
                    .commit()
            }
        } else {
            cvPlayerView.visibility = View.GONE
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

        initListeners()
        RxBus.subscribe({
            when (it) {
                is ShowSnackBar -> {
                    displayMaterialSnackBar(it.message, ConnectivityType.OTHER, rootView)
                }
                is ShareSnackBar -> {
                    displayMaterialSnackBar(
                        it.message,
                        ConnectivityType.OTHER,
                        rootView,
                        getString(R.string.share_text), it.title, it.extra
                    )
                }
            }
        }, {
            it.printStackTrace()
        })
    }

    private fun initListeners() {
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

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        nestedScroll.setOnScrollChangeListener { scrollView: NestedScrollView?, _: Int, scrollY: Int, _: Int, _: Int ->
            if (scrollView != null) {
                val totalHeight =
                    scrollView.getChildAt(0).measuredHeight - scrollView.measuredHeight
                readPercent = (scrollY.toDouble() / totalHeight.toDouble() * 100).roundToInt()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        System.gc()
    }
}