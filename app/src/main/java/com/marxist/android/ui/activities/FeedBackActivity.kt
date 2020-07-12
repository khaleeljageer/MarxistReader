package com.marxist.android.ui.activities

import android.os.Bundle
import android.telephony.PhoneNumberUtils
import androidx.appcompat.app.AlertDialog
import com.marxist.android.R
import com.marxist.android.model.ConnectivityType
import com.marxist.android.ui.base.BaseActivity
import com.marxist.android.utils.DeviceUtils
import com.marxist.android.utils.PrintLog
import com.marxist.android.utils.api.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_feedback.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class FeedBackActivity : BaseActivity() {
    private var disposable: Disposable? = null

    /* URL : https://docs.google.com/forms/d/e/1FAIpQLSfTqVr8z77QxxclC_ZnvsQfOc67F1Wjw07giA2jxoXpFtuvLg/formResponse
            * name : entry_1191964018
            * phone : entry_663380355
            * feedback : entry_866777428 */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        btnSubmit.setOnClickListener {
            submit()
        }
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

        val name = edtName.text.toString()
        val phone = edtPhone.text.toString()
        val comments = edtComments.text.toString()

        if (name.isBlank() || name.isEmpty()) {
            edtName.error = "Mandatory"
            return
        }

        if (phone.isBlank() || phone.isEmpty()) {
            edtPhone.error = "Mandatory"
            return
        }

        if (!PhoneNumberUtils.isGlobalPhoneNumber(phone)) {
            edtPhone.error = "Invalid phone number"
            return
        }

        if (comments.isBlank() || comments.isEmpty()) {
            edtComments.error = "Mandatory"
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

                edtName.setText("")
                edtPhone.setText("")
                edtComments.setText("")

                displayMaterialSnackBar(
                    getString(R.string.thanks_feedback),
                    ConnectivityType.OTHER,
                    rootView
                )
            }, { error ->
                dialog.dismiss()
                displayMaterialSnackBar(
                    getString(R.string.try_later),
                    ConnectivityType.OTHER,
                    rootView
                )
            })
    }
}