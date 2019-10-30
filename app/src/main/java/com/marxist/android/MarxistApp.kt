package com.marxist.android

import android.app.Application
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import com.marxist.android.database.AppDatabase
import com.marxist.android.utils.api.ApiClient
import com.marxist.android.utils.network.NetworkSchedulerService


class MarxistApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AppDatabase.getAppDatabase(applicationContext)
        scheduleJob()
        ApiClient.setApiService()
    }

    private fun scheduleJob() {
        val myJob = JobInfo.Builder(0, ComponentName(this, NetworkSchedulerService::class.java))
            .setMinimumLatency(1000)
            .setOverrideDeadline(2000)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPersisted(true)
            .build()

        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(myJob)
    }
}