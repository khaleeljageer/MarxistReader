package org.cpimtn.marxist.android.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.cpimtn.marxist.android.data.worker.Sync

@HiltAndroidApp
class MarxistReaderApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Sync.initialize(applicationContext)
    }
}
