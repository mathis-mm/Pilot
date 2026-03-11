package com.example.pilot.update

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pilot.notifications.NotificationHelper

class UpdateWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val update = UpdateChecker.checkForUpdate(applicationContext)
        if (update != null) {
            NotificationHelper.showUpdateNotification(applicationContext, update.versionName)
        }
        return Result.success()
    }
}
