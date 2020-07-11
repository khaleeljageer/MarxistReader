package com.marxist.android.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.core.content.ContextCompat
import com.marxist.android.R
import com.marxist.android.ui.base.BaseActivity
import com.marxist.android.utils.PrintLog
import kotlinx.android.synthetic.main.activity_search.*
import java.util.*


class SearchActivity : BaseActivity() {
    companion object {
        const val RECOGNIZER_REQ_CODE = 1234
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(window) {
            statusBarColor = ContextCompat.getColor(baseContext, R.color.colorSecondaryDark)
        }
        setContentView(R.layout.activity_search)

        ivVoiceSearch.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.marxist.android")
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ta-IN")
            startActivityForResult(intent, RECOGNIZER_REQ_CODE)
        }

        ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        PrintLog.debug("Khaleel", "resultCode - $resultCode == requestCode - $requestCode")
        when (requestCode) {
            RECOGNIZER_REQ_CODE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val result: ArrayList<String>? =
                        data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

                    if (result != null) {
                        edtSearch.text!!.clear()
                        edtSearch.append(result[0])
                    }
                }
            }
        }
    }
}