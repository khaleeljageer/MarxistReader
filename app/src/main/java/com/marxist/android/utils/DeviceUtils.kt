package com.marxist.android.utils

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.ConnectivityManager
import com.marxist.android.R


object DeviceUtils {

    fun isConnectedToNetwork(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    fun shareIntent(
        subject: String,
        textExtra: String,
        baseContext: Context
    ) {
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.type = "text/plain"
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        sendIntent.putExtra(Intent.EXTRA_TEXT, textExtra)

        val shareIntent = Intent.createChooser(
            sendIntent,
            baseContext.getString(R.string.share_text)
        )
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        baseContext.startActivity(shareIntent)
    }

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
}