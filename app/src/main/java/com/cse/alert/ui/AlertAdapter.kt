package com.cse.alert.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cse.alert.R
import com.cse.alert.databinding.ItemAlertBinding
import com.cse.alert.model.AlertCondition
import com.cse.alert.model.AlertStatus
import com.cse.alert.model.PriceAlert
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AlertAdapter(
    private val onReactivate: (PriceAlert) -> Unit,
    private val onDisable:    (PriceAlert) -> Unit,
    private val onDelete:     (PriceAlert) -> Unit
) : ListAdapter<PriceAlert, AlertAdapter.ViewHolder>(DIFF) {

    private val fmt  = NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 2 }
    private val sdf  = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())

    fun getAlertAt(position: Int): PriceAlert = getItem(position)

    inner class ViewHolder(private val b: ItemAlertBinding) : RecyclerView.ViewHolder(b.root) {

        fun bind(alert: PriceAlert) {
            // Symbol & company
            b.tvSymbol.text      = alert.symbol.substringBefore(".")
            b.tvCompanyName.text = alert.companyName

            // Direction chip
            val isAbove = alert.condition == AlertCondition.ABOVE
            b.tvCondition.text = if (isAbove) "▲ ABOVE" else "▼ BELOW"
            b.tvCondition.setTextColor(
                b.root.context.getColor(if (isAbove) R.color.gain_green else R.color.loss_red)
            )

            // Target price
            b.tvTargetPrice.text = "Target: LKR ${fmt.format(alert.targetPrice)}"

            // Current price
            if (alert.currentPrice > 0) {
                b.tvCurrentPrice.text    = "Current: LKR ${fmt.format(alert.currentPrice)}"
                b.tvCurrentPrice.visibility = View.VISIBLE
            } else {
                b.tvCurrentPrice.visibility = View.GONE
            }

            // Note
            if (alert.note.isNotEmpty()) {
                b.tvNote.text       = alert.note
                b.tvNote.visibility = View.VISIBLE
            } else {
                b.tvNote.visibility = View.GONE
            }

            // Status badge
            when (alert.status) {
                AlertStatus.ACTIVE -> {
                    b.tvStatus.text = "● ACTIVE"
                    b.tvStatus.setTextColor(b.root.context.getColor(R.color.gain_green))
                    b.btnReactivate.visibility = View.GONE
                    b.btnDisable.visibility    = View.VISIBLE
                }
                AlertStatus.TRIGGERED -> {
                    b.tvStatus.text = "🔔 TRIGGERED"
                    b.tvStatus.setTextColor(b.root.context.getColor(R.color.color_triggered))
                    b.tvTriggeredAt.text = "at ${sdf.format(Date(alert.triggeredAt ?: 0L))}"
                    b.tvTriggeredAt.visibility = View.VISIBLE
                    b.btnReactivate.visibility = View.VISIBLE
                    b.btnDisable.visibility    = View.GONE
                }
                AlertStatus.DISABLED -> {
                    b.tvStatus.text = "○ DISABLED"
                    b.tvStatus.setTextColor(b.root.context.getColor(R.color.color_disabled))
                    b.btnReactivate.visibility = View.VISIBLE
                    b.btnDisable.visibility    = View.GONE
                }
                AlertStatus.SNOOZED -> {
                    b.tvStatus.text = "💤 SNOOZED"
                    b.tvStatus.setTextColor(b.root.context.getColor(R.color.color_snoozed))
                    b.btnReactivate.visibility = View.VISIBLE
                    b.btnDisable.visibility    = View.GONE
                }
            }

            b.btnReactivate.setOnClickListener { onReactivate(alert) }
            b.btnDisable.setOnClickListener    { onDisable(alert) }
            b.btnDelete.setOnClickListener     { onDelete(alert) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemAlertBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<PriceAlert>() {
            override fun areItemsTheSame(a: PriceAlert, b: PriceAlert) = a.id == b.id
            override fun areContentsTheSame(a: PriceAlert, b: PriceAlert) = a == b
        }
    }
}
