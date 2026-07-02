package com.cse.alert.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cse.alert.data.NotificationHelper
import com.cse.alert.databinding.ActivityMainBinding
import com.cse.alert.model.AlertStatus
import com.cse.alert.worker.PriceCheckWorker
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var alertAdapter: AlertAdapter

    // Permission launcher for POST_NOTIFICATIONS (Android 13+)
    private val notifPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Toast.makeText(this, "Notifications enabled ✓", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this,
                "Notifications blocked — you won't receive price alerts",
                Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        NotificationHelper.createChannels(this)
        requestNotificationPermission()
        PriceCheckWorker.schedule(this)   // ensure periodic checks are running

        setupRecyclerView()
        setupFab()
        observeViewModel()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun setupRecyclerView() {
        alertAdapter = AlertAdapter(
            onReactivate = { alert -> viewModel.reactivateAlert(alert.id) },
            onDisable    = { alert -> viewModel.disableAlert(alert.id) },
            onDelete     = { alert -> confirmDelete(alert.id, alert.symbol) }
        )

        binding.rvAlerts.apply {
            adapter = alertAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        // Swipe left to delete
        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder) = false

            override fun onSwiped(vh: RecyclerView.ViewHolder, direction: Int) {
                val alert = alertAdapter.getAlertAt(vh.adapterPosition)
                confirmDelete(alert.id, alert.symbol) {
                    // If cancelled, restore item
                    alertAdapter.notifyItemChanged(vh.adapterPosition)
                }
            }
        }
        ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.rvAlerts)

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshNow()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddAlertActivity::class.java))
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.alerts.collect { alerts ->
                    alertAdapter.submitList(alerts)

                    // Summary counts in toolbar subtitle
                    val active    = alerts.count { it.status == AlertStatus.ACTIVE }
                    val triggered = alerts.count { it.status == AlertStatus.TRIGGERED }
                    supportActionBar?.subtitle = when {
                        alerts.isEmpty() -> "No alerts set"
                        triggered > 0    -> "$active active · $triggered triggered"
                        else             -> "$active active alert${if (active != 1) "s" else ""}"
                    }

                    binding.tvEmpty.visibility =
                        if (alerts.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }

        viewModel.message.observe(this) { msg ->
            msg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearMessage()
            }
        }
    }

    private fun confirmDelete(id: Int, symbol: String, onCancel: (() -> Unit)? = null) {
        AlertDialog.Builder(this)
            .setTitle("Delete Alert")
            .setMessage("Delete price alert for ${symbol.substringBefore(".")}?")
            .setPositiveButton("Delete") { _, _ -> viewModel.deleteAlert(id) }
            .setNegativeButton("Cancel") { _, _ -> onCancel?.invoke() }
            .show()
    }
}
