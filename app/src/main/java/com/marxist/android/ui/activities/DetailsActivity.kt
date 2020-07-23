package com.marxist.android.ui.activities

import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.marxist.android.R
import com.marxist.android.database.entities.LocalFeeds
import com.marxist.android.model.*
import com.marxist.android.ui.base.BaseActivity
import com.marxist.android.ui.fragments.player.AudioPlayerFragment
import com.marxist.android.ui.fragments.tune.TuneSheetFragment
import com.marxist.android.utils.AppPreference.get
import com.marxist.android.utils.DeviceUtils
import com.marxist.android.utils.RxBus
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : BaseActivity() {
    private lateinit var disposable: Disposable
    private var article: LocalFeeds? = null

    private val tuneSheet by lazy {
        TuneSheetFragment()
    }

    companion object {
        const val ARTICLE = "article"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.let {
            if (it.itemId == R.id.menu_share) {
                DeviceUtils.shareIntent(article!!.title, article!!.link, applicationContext)
                return true
            } else if (it.itemId == R.id.menu_tune) {
                tuneSheet.show(supportFragmentManager, tuneSheet.tag)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun applyFont(fontId: Int) {
        val typeface = ResourcesCompat.getFont(baseContext, fontId)
        typeface?.let { f ->
            txtContent.typeface = f
            txtTitle.typeface = f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        article = intent.getSerializableExtra(ARTICLE) as LocalFeeds

        txtTitle.text = article!!.title

        val selectedFont = appPreference[getString(R.string.pref_key_preferred_font), "Hind"]
        val fontsId = arrayOf(
            R.font.arima_madurai, R.font.catamaran, R.font.hind_regular,
            R.font.meera_inimai, R.font.mukta_malar, R.font.pavanam
        )
        val fonts = arrayOf("Arima", "Catamaran", "Hind", "Meera", "Mukta", "Pavanam")
        val fontId = fontsId[fonts.indexOf(selectedFont)]
        applyFont(fontId)

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
        disposable = RxBus.subscribe({
            when (it) {
                is FontChange -> {
                    applyFont(it.fontId)
                }
                is FontSizeChange -> {
                    txtContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, it.fontSize)
                }
                is ShowSnackBar -> displayMaterialSnackBar(
                    it.message,
                    ConnectivityType.OTHER,
                    rootView
                )
                is ReaderBgChange -> {
                    if (it.color == 0xff282828) {
                        txtTitle.setTextColor(0xffffffff.toInt())
                        txtContent.setTextColor(0xffffffff.toInt())
                    } else {
                        txtTitle.setTextColor(0xff000000.toInt())
                        txtContent.setTextColor(0xff000000.toInt())
                    }

                    it.color.toInt().run {
                        txtContent.setBackgroundColor(this)
                        txtTitle.setBackgroundColor(this)
                        toolbar.setBackgroundColor(this)
                        window.statusBarColor = this
                    }
                }
            }
        }, {
            it.printStackTrace()
        })
    }

    private fun initListeners() {
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::disposable.isInitialized) {
            disposable.dispose()
        }
        System.gc()
    }
}