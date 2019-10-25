package com.marxist.android.utils.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.marxist.android.utils.DeviceUtils

class ConnectivityReceiver internal constructor(private val mConnectivityReceiverListener: ConnectivityReceiverListener) :
    BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        mConnectivityReceiverListener.onNetworkConnectionChanged(DeviceUtils.isConnectedToNetwork(context))
    }
}