package com.cse.alert.ui

import android.app.Application
import androidx.lifecycle.*
import com.cse.alert.data.AlertRepository
import com.cse.alert.model.*
import com.cse.alert.worker.PriceCheckWorker
import kotlinx.coroutines.launch

class AddAlertViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AlertRepository(app)

    private val _searchResults = MutableLiveData<List<SymbolSearchResult>>(CSE_POPULAR)
    val searchResults: LiveData<List<SymbolSearchResult>> = _searchResults

    private val _selectedStock = MutableLiveData<SymbolSearchResult?>()
    val selectedStock: LiveData<SymbolSearchResult?> = _selectedStock

    private val _currentPrice = MutableLiveData<Double?>()
    val currentPrice: LiveData<Double?> = _currentPrice

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _navigateBack = MutableLiveData(false)
    val navigateBack: LiveData<Boolean> = _navigateBack

    fun search(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _searchResults.value = repo.searchSymbols(query)
            _isLoading.value = false
        }
    }

    fun selectStock(stock: SymbolSearchResult) {
        _selectedStock.value = stock
        // Fetch live price for the selected stock
        viewModelScope.launch {
            _isLoading.value = true
            val info = repo.fetchCompanyInfo(stock.symbol)
            _currentPrice.value = info?.lastTradedPrice
            _isLoading.value = false
        }
    }

    fun saveAlert(
        targetPrice: Double,
        condition: AlertCondition,
        note: String
    ) {
        val stock = _selectedStock.value ?: run {
            _message.value = "Please select a company first"
            return
        }
        if (targetPrice <= 0) {
            _message.value = "Enter a valid target price"
            return
        }

        viewModelScope.launch {
            val alert = PriceAlert(
                symbol = stock.symbol,
                companyName = stock.name,
                targetPrice = targetPrice,
                condition = condition,
                currentPrice = _currentPrice.value ?: 0.0,
                note = note
            )
            repo.addAlert(alert)
            // Run an immediate check so user gets notified right away if already met
            PriceCheckWorker.runNow(getApplication())
            _message.value = "Alert saved!"
            _navigateBack.value = true
        }
    }

    fun clearMessage() { _message.value = null }
}
