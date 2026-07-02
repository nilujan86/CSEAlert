package com.cse.alert.worker

import android.content.Context
import androidx.work.*
import com.cse.alert.data.AlertRepository
import com.cse.alert.data.NotificationHelper
import java.util.concurrent.TimeUnit

class PriceCheckWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val repository = AlertRepository(context)
            val triggered = repository.checkAllAlerts()

            // Fire a notification for each newly triggered alert
            triggered.forEach { alert ->
                NotificationHelper.sendPriceAlert(context, alert)
            }

            Result.success()
        } catch (e: Exception) {
            // Retry on failure (network hiccup etc.)
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "cse_price_check"

        /**
         * Schedules periodic price checks every 15 minutes (WorkManager minimum).
         * During market hours the check fires every 15 min;
         * WorkManager batches and defers it outside market hours automatically
         * when using constraints.
         */
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<PriceCheckWorker>(
                15, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 5, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,   // don't reset timer if already scheduled
                request
            )
        }

        /** One-shot immediate check — called when user adds a new alert */
        fun runNow(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = OneTimeWorkRequestBuilder<PriceCheckWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueue(request)
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
