package com.marxist.android.ui.activities

import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.marxist.android.R
import com.marxist.android.databinding.ActivityFeedbackBinding
import com.marxist.android.model.ConnectivityType
import com.marxist.android.ui.base.BaseActivity
import com.marxist.android.utils.DeviceUtils
import com.marxist.android.utils.api.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class FeedBackActivity : BaseActivity() {
    private var disposable: Disposable? = null

    /* URL : https://docs.google.com/forms/d/e/1FAIpQLSfTqVr8z77QxxclC_ZnvsQfOc67F1Wjw07giA2jxoXpFtuvLg/formResponse
            * name : entry_1191964018
            * phone : entry_663380355
            * feedback : entry_866777428 */

    private val binding by lazy {
        ActivityFeedbackBinding.inflate(layoutInflater)
    }

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

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
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

        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://docs.google.com/forms/d/")
            .build().create(ApiService::class.java)

        disposable = retrofit.postFeedBack("application/json", name, phone, comments)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                dialog.dismiss()

                binding.edtName.setText("")
                binding.edtPhone.setText("")
                binding.edtComments.setText("")

                displayMaterialSnackBar(
                    getString(R.string.thanks_feedback),
                    ConnectivityType.OTHER,
                    binding.rootView
                )
            }, { error ->
                dialog.dismiss()
                displayMaterialSnackBar(
                    getString(R.string.try_later),
                    ConnectivityType.OTHER,
                    binding.rootView
                )
            })
    }
}