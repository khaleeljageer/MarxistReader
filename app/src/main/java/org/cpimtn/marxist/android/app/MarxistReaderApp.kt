package org.cpimtn.marxist.android.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.jskaleel.epub.EpubApplication
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.cpimtn.marxist.android.data.worker.Sync
import org.cpimtn.marxist.android.domain.usecase.GetSettingsFlowUseCase
import javax.inject.Inject

@HiltAndroidApp
class MarxistReaderApp : EpubApplication(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var getSettingsFlowUseCase: GetSettingsFlowUseCase

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        val language = runBlocking { getSettingsFlowUseCase().first().language }
        LocaleController.apply(language)
        Sync.initialize(applicationContext)
    }
}
