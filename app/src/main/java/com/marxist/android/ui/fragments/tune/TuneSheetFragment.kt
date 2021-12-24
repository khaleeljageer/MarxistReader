package com.marxist.android.ui.fragments.tune

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import com.marxist.android.R
import com.marxist.android.databinding.DetailsTuneViewBinding
import com.marxist.android.model.FontChange
import com.marxist.android.model.FontSizeChange
import com.marxist.android.model.ReaderBgChange
import com.marxist.android.utils.AppPreference.get
import com.marxist.android.utils.AppPreference.set
import com.marxist.android.utils.RxBus
import org.koin.android.ext.android.inject

class TuneSheetFragment : BottomSheetDialogFragment() {
    private val appPreference: SharedPreferences by inject()
    private var binding: DetailsTuneViewBinding? = null

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DetailsTuneViewBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (binding != null) {
            val selectedFont = appPreference[getString(R.string.pref_key_preferred_font), "Hind"]
            val fontsId = arrayOf(
                R.font.arima_madurai, R.font.catamaran, R.font.hind_regular,
                R.font.meera_inimai, R.font.mukta_malar, R.font.pavanam
            )
            val fonts = arrayOf("Arima", "Catamaran", "Hind", "Meera", "Mukta", "Pavanam")
            for (name in fonts) {
                val tab = binding!!.fontTabs.newTab()
                tab.text = name
                binding!!.fontTabs.addTab(tab)
            }
            val index = fonts.indexOf(selectedFont)
            binding!!.fontTabs.getTabAt(index)?.select()

            val fontSize = appPreference[getString(R.string.pref_key_font_size), 14]
            binding!!.fontSizeSeek.progress = fontSize

            val colors = mapOf(
                R.id.checkWhite to 0xffFFFFFF,
                R.id.checkNight to 0xff282828,
                R.id.checkTradition to 0xffF8F2E5
            )

            binding!!.radioGroup.setOnCheckedChangeListener { _, p1 ->
                if (p1 != -1) {
                    appPreference[getString(R.string.pref_key_reader_bg)] = p1
                    val color = colors[p1]
                    color?.let {
                        RxBus.publish(ReaderBgChange(it))
                    }
                }
            }

            binding!!.fontSizeSeek.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    if (p2) {
                        appPreference[getString(R.string.pref_key_font_size)] = p1
                        RxBus.publish(FontSizeChange(p1.toFloat()))
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                }
            })

            binding!!.fontTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.let {
                        val pos = it.position
                        RxBus.publish(FontChange(fontsId[pos]))
                        appPreference[getString(R.string.pref_key_preferred_font)] = fonts[pos]
                    }
                }
            })
        }
    }
}