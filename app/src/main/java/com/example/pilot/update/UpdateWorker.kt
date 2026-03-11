package com.example.pilot.update

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pilot.notifications.NotificationHelper

class UpdateWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d("UpdateWorker", "Checking for updates...")
        val update = UpdateChecker.checkForUpdate(applicationContext)
        if (update != null) {
            Log.d("UpdateWorker", "Update found: ${update.versionName}")
            NotificationHelper.showUpdateNotification(applicationContext, update.versionName)
            // Auto-download the APK so it's ready when user opens the app
            UpdateManager.downloadAndInstall(applicationContext, update)
        } else {
            Log.d("UpdateWorker", "No update available")
        }
        return Result.success()
    }
}
