package com.marxist.android.ui.activities.details

import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import com.marxist.android.R
import com.marxist.android.data.model.WPPost
import com.marxist.android.databinding.ActivityDetailsBinding
import com.marxist.android.model.FontChange
import com.marxist.android.model.FontSizeChange
import com.marxist.android.model.ReaderBgChange
import com.marxist.android.model.ShowSnackBar
import com.marxist.android.ui.base.BaseActivity
import com.marxist.android.ui.fragments.player.AudioPlayerFragment
import com.marxist.android.ui.fragments.tune.TuneSheetFragment
import com.marxist.android.utils.AppPreference.get
import com.marxist.android.utils.DeviceUtils
import com.marxist.android.utils.EventBus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DetailsActivity : BaseActivity() {
    private var article: WPPost? = null

    @Inject
    lateinit var eventBus: EventBus

    private val detailsViewModel: DetailsViewModel by viewModels()

    private val tuneSheet by lazy {
        TuneSheetFragment()
    }

    companion object {
        const val ARTICLE = "article"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.let {
            if (it.itemId == R.id.menu_share) {
                DeviceUtils.shareIntent(
                    article!!.title.rendered,
                    article!!.link,
                    applicationContext
                )
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
            binding.txtContent.typeface = f
            binding.txtTitle.typeface = f
        }
    }

    private val binding by lazy {
        ActivityDetailsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""

        article = intent.getParcelableExtra(ARTICLE) as? WPPost

        article?.let {
            detailsViewModel.triggerArticleLink(it.link)

            binding.txtTitle.text =
                HtmlCompat.fromHtml(it.title.rendered, HtmlCompat.FROM_HTML_MODE_COMPACT)

            val type = 1
            if (it.audioUrl.isNotEmpty()) {
                binding.cvPlayerView.visibility = View.VISIBLE
                MainScope().launch {
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.animator.slide_in_from_bottom, R.animator.slide_out_to_bottom,
                            R.animator.slide_in_from_bottom, R.animator.slide_out_to_bottom
                        )
                        .replace(
                            binding.flAudioPlayer.id,
                            AudioPlayerFragment.newInstance(article!!, type)
                        )
                        .commit()
                }
            } else {
                binding.cvPlayerView.visibility = View.GONE
            }

            var content: String = it.content.rendered
            val fontSize = appPreference[getString(R.string.pref_key_font_size), 14]
            try {
                val regex1 = Regex("(?s)<div class=\"wpcnt\">.*?</div>")
                content = content.replace(regex1, "")
                val regex2 = Regex("(?s)<script type=\"text/javascript\">.*?</script>")
                content = content.replace(regex2, "")
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                binding.txtContent.text =
                    HtmlCompat.fromHtml(content, HtmlCompat.FROM_HTML_MODE_LEGACY)
                binding.txtContent.textSize = fontSize.toFloat()
            }
        }
        initFont()
        initListeners()


        lifecycleScope.launch {
            eventBus.events.collect {
                when (it) {
                    is FontChange -> {
                        applyFont(it.fontId)
                    }
                    is FontSizeChange -> {
                        binding.txtContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, it.fontSize)
                    }
                    is ShowSnackBar -> displayMaterialSnackBar(
                        it.message,
                        binding.rootView
                    )
                    is ReaderBgChange -> {
                        if (it.color == 0xff282828) {
                            binding.txtTitle.setTextColor(0xffffffff.toInt())
                            binding.txtContent.setTextColor(0xffffffff.toInt())
                        } else {
                            binding.txtTitle.setTextColor(0xff000000.toInt())
                            binding.txtContent.setTextColor(0xff000000.toInt())
                        }

                        it.color.toInt().run {
                            binding.txtContent.setBackgroundColor(this)
                            binding.txtTitle.setBackgroundColor(this)
                            binding.toolbar.setBackgroundColor(this)
                            window.statusBarColor = this
                        }
                    }
                }
            }
        }
    }

    private fun initFont() {
        val selectedFont = appPreference[getString(R.string.pref_key_preferred_font), "Hind"]
        val fontsId = arrayOf(
            R.font.arima_madurai, R.font.catamaran, R.font.hind_regular,
            R.font.meera_inimai, R.font.mukta_malar, R.font.pavanam
        )
        val fonts = arrayOf("Arima", "Catamaran", "Hind", "Meera", "Mukta", "Pavanam")
        val fontId = fontsId[fonts.indexOf(selectedFont)]
        applyFont(fontId)
    }

    private fun initListeners() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        System.gc()
    }

}