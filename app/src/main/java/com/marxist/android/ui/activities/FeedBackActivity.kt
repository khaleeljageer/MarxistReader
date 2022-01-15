package com.marxist.android.ui.activities

import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.marxist.android.R
import com.marxist.android.data.api.FeedbackHelper
import com.marxist.android.databinding.ActivityFeedbackBinding
import com.marxist.android.ui.base.BaseActivity
import com.marxist.android.utils.DeviceUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FeedBackActivity : BaseActivity() {

    /* URL : https://docs.google.com/forms/d/e/1FAIpQLSfTqVr8z77QxxclC_ZnvsQfOc67F1Wjw07giA2jxoXpFtuvLg/formResponse
            * name : entry_1191964018
            * phone : entry_663380355
            * feedback : entry_866777428 */

    private val binding by lazy {
        ActivityFeedbackBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var feedbackHelper: FeedbackHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        supportActionBar?.apply {
            title = getString(R.string.feed_back)
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp)
        }

        binding.btnSubmit.setOnClickListener {
            submit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    private fun submit() {
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        builder.setCancelable(false)
        builder.setView(R.layout.loader_view)
        val dialog = builder.create()

        val name = binding.edtName.text.toString()
        val phone = binding.edtPhone.text.toString()
        val comments = binding.edtComments.text.toString()

        if (name.isBlank() || name.isEmpty()) {
            binding.edtName.error = "Mandatory"
            return
        }

        if (phone.isBlank() || phone.isEmpty()) {
            binding.edtPhone.error = "Mandatory"
            return
        }

        if (!PhoneNumberUtils.isGlobalPhoneNumber(phone)) {
            binding.edtPhone.error = "Invalid phone number"
            return
        }

        if (comments.isBlank() || comments.isEmpty()) {
            binding.edtComments.error = "Mandatory"
            return
        }

        dialog.show()
        DeviceUtils.hideSoftKeyboard(this)
        lifecycleScope.launch(Dispatchers.IO) {
            val response = feedbackHelper.postFeedBack("application/json", name, phone, comments)
            if (response.isSuccessful) {
                lifecycleScope.launch(Dispatchers.Main) {
                    dialog.dismiss()

                    binding.edtName.setText("")
                    binding.edtPhone.setText("")
                    binding.edtComments.setText("")

                    displayMaterialSnackBar(
                        getString(R.string.thanks_feedback),
                        binding.rootView
                    )
                }
            } else {
                lifecycleScope.launch(Dispatchers.Main) {
                    dialog.dismiss()
                    displayMaterialSnackBar(
                        getString(R.string.try_later),
                        binding.rootView
                    )
                }
            }
        }
    }
}