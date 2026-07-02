package com.cse.alert.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.cse.alert.R
import com.cse.alert.model.AlertCondition
import com.cse.alert.model.PriceAlert
import com.cse.alert.receiver.AlertActionReceiver
import com.cse.alert.ui.MainActivity
import java.text.NumberFormat
import java.util.Locale

object NotificationHelper {

    const val CHANNEL_ALERTS   = "cse_price_alerts"
    const val CHANNEL_SERVICE  = "cse_service"

    private val fmt = NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 2 }

    fun createChannels(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Price alert channel — high importance so it pops heads-up
        nm.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ALERTS,
                "Price Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies when a CSE stock reaches your target price"
                enableVibration(true)
                enableLights(true)
            }
        )

        // Background service channel
        nm.createNotificationChannel(
            NotificationChannel(
                CHANNEL_SERVICE,
                "Background Price Check",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Runs silently to monitor stock prices"
            }
        )
    }

    fun sendPriceAlert(context: Context, alert: PriceAlert) {
        val direction = if (alert.condition == AlertCondition.ABOVE) "risen above" else "fallen below"
        val emoji     = if (alert.condition == AlertCondition.ABOVE) "🚀" else "📉"

        val title = "$emoji ${alert.symbol.substringBefore(".")} Alert Triggered!"
        val body  = "${alert.companyName} has $direction your target of " +
                    "LKR ${fmt.format(alert.targetPrice)}.\n" +
                    "Current price: LKR ${fmt.format(alert.currentPrice)}"

        // Tap → open app
        val openIntent = PendingIntent.getActivity(
            context, alert.id,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("alert_id", alert.id)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Snooze action — reactivates alert after 30 min
        val snoozePi = PendingIntent.getBroadcast(
            context, alert.id + 10000,
            Intent(context, AlertActionReceiver::class.java).apply {
                action = AlertActionReceiver.ACTION_SNOOZE
                putExtra("alert_id", alert.id)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Dismiss action
        val dismissPi = PendingIntent.getBroadcast(
            context, alert.id + 20000,
            Intent(context, AlertActionReceiver::class.java).apply {
                action = AlertActionReceiver.ACTION_DISMISS
                putExtra("alert_id", alert.id)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ALERTS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(openIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(R.drawable.ic_snooze, "Snooze 30m", snoozePi)
            .addAction(R.drawable.ic_dismiss, "Dismiss", dismissPi)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(alert.id, notification)
        } catch (e: SecurityException) {
            // POST_NOTIFICATIONS permission not granted — handled in MainActivity
        }
    }
}
