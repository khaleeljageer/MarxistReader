package com.marxist.android.utils.network

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.content.IntentFilter
import com.marxist.android.R
import com.marxist.android.model.ConnectivityType
import com.marxist.android.model.NetWorkMessage
import com.marxist.android.utils.AppConstants
import com.marxist.android.utils.RxBus

class NetworkSchedulerService : JobService(), ConnectivityReceiverListener {

    private lateinit var mConnectivityReceiver: ConnectivityReceiver

    override fun onCreate() {
        super.onCreate()
        mConnectivityReceiver = ConnectivityReceiver(this);
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return Service.START_NOT_STICKY
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        try {
            unregisterReceiver(mConnectivityReceiver)
        } catch (e: Exception) {

        }
        return true
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        registerReceiver(mConnectivityReceiver, IntentFilter(AppConstants.CONNECTIVITY_ACTION))
        return true
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        val message =
            if (isConnected) getString(R.string.connected_to_internet) else getString(R.string.no_internet)
        RxBus.publish(
            NetWorkMessage(
                message,
                if (isConnected) ConnectivityType.CONNECTED else ConnectivityType.LOST
            )
        )
    }
}