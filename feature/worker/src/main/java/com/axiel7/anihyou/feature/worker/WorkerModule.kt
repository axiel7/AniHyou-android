package com.axiel7.anihyou.feature.worker

import androidx.work.WorkManager
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module

val workerModule = module {
    single { WorkManager.getInstance(get()) }
    workerOf(::NotificationWorker)
}