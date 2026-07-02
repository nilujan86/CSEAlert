package com.cse.alert.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cse.alert.databinding.ActivityAddAlertBinding
import com.cse.alert.model.AlertCondition
import com.cse.alert.model.SymbolSearchResult
import java.text.NumberFormat
import java.util.Locale

class AddAlertActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddAlertBinding
    private val viewModel: AddAlertViewModel by viewModels()
    private lateinit var searchAdapter: SearchAdapter
    private val fmt = NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 2 }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "New Price Alert"
        }

        setupSearch()
        setupForm()
        observeViewModel()
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }

    // ── Search panel ──────────────────────────────────────────────────────────

    private fun setupSearch() {
        searchAdapter = SearchAdapter { stock -> onStockSelected(stock) }

        binding.rvSearch.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(this@AddAlertActivity)
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.search(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
        })

        // Load full list immediately
        viewModel.search("")
    }

    private fun onStockSelected(stock: SymbolSearchResult) {
        viewModel.selectStock(stock)
        // Collapse search, show form
        binding.cardSearch.visibility    = View.GONE
        binding.cardForm.visibility      = View.VISIBLE
        binding.tvSelectedSymbol.text    = stock.symbol.substringBefore(".")
        binding.tvSelectedName.text      = stock.name
        binding.btnChangeStock.visibility = View.VISIBLE
    }

    // ── Alert form ────────────────────────────────────────────────────────────

    private fun setupForm() {
        binding.btnChangeStock.setOnClickListener {
            binding.cardSearch.visibility = View.VISIBLE
            binding.cardForm.visibility   = View.GONE
            binding.etSearch.text?.clear()
            viewModel.search("")
        }

        binding.btnSave.setOnClickListener {
            val priceStr = binding.etTargetPrice.text.toString().trim()
            val price    = priceStr.toDoubleOrNull()
            if (price == null || price <= 0) {
                binding.etTargetPrice.error = "Enter a valid price"
                return@setOnClickListener
            }

            val condition = if (binding.rbAbove.isChecked)
                AlertCondition.ABOVE else AlertCondition.BELOW

            val note = binding.etNote.text.toString().trim()

            viewModel.saveAlert(price, condition, note)
        }
    }

    // ── Observe ───────────────────────────────────────────────────────────────

    private fun observeViewModel() {
        viewModel.searchResults.observe(this) { results ->
            searchAdapter.submitList(results)
        }

        viewModel.isLoading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.currentPrice.observe(this) { price ->
            if (price != null && price > 0) {
                binding.tvCurrentPrice.text = "Current price: LKR ${fmt.format(price)}"
                binding.tvCurrentPrice.visibility = View.VISIBLE
                // Pre-fill target price with current price as hint
                if (binding.etTargetPrice.text.isNullOrEmpty()) {
                    binding.etTargetPrice.hint = fmt.format(price)
                }
            } else {
                binding.tvCurrentPrice.visibility = View.GONE
            }
        }

        viewModel.message.observe(this) { msg ->
            msg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearMessage()
            }
        }

        viewModel.navigateBack.observe(this) { go ->
            if (go) finish()
        }
    }
}
