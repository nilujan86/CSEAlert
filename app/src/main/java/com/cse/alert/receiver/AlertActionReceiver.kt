package com.cse.alert.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.cse.alert.data.AlertDatabase
import com.cse.alert.worker.PriceCheckWorker
import kotlinx.coroutines.*

class AlertActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_SNOOZE  = "com.cse.alert.ACTION_SNOOZE"
        const val ACTION_DISMISS = "com.cse.alert.ACTION_DISMISS"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val alertId = intent.getIntExtra("alert_id", -1)
        if (alertId == -1) return

        // Dismiss the notification
        NotificationManagerCompat.from(context).cancel(alertId)

        val dao = AlertDatabase.getInstance(context).alertDao()

        when (intent.action) {
            ACTION_SNOOZE -> {
                // Reactivate after 30 minutes by re-enabling and scheduling immediate check
                CoroutineScope(Dispatchers.IO).launch {
                    dao.reactivate(alertId)
                    delay(30 * 60 * 1000L)   // 30 minutes
                    PriceCheckWorker.runNow(context)
                }
            }
            ACTION_DISMISS -> {
                // Keep as TRIGGERED (already set) — user just dismissed notification
            }
        }
    }
}
