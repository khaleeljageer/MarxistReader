package org.cpimtn.marxist.android.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.jskaleel.epub.EpubApplication
import dagger.hilt.android.HiltAndroidApp
import org.cpimtn.marxist.android.data.worker.Sync
import javax.inject.Inject

@HiltAndroidApp
class MarxistReaderApp : EpubApplication(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        Sync.initialize(applicationContext)
    }
}
