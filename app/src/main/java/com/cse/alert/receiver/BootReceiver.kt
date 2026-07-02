package com.cse.alert.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.cse.alert.worker.PriceCheckWorker

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            PriceCheckWorker.schedule(context)
        }
    }
}
