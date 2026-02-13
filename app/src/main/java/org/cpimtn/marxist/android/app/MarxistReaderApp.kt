package org.cpimtn.marxist.android.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MarxistReaderApp : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}
