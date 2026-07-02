package com.cse.alert.ui

import android.app.Application
import androidx.lifecycle.*
import com.cse.alert.data.AlertRepository
import com.cse.alert.model.*
import com.cse.alert.worker.PriceCheckWorker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AlertRepository(app)

    val alerts = repo.allAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // ── Alert actions ─────────────────────────────────────────────────────────

    fun deleteAlert(id: Int) {
        viewModelScope.launch {
            repo.deleteAlert(id)
            _message.value = "Alert deleted"
        }
    }

    fun reactivateAlert(id: Int) {
        viewModelScope.launch {
            repo.reactivateAlert(id)
            PriceCheckWorker.runNow(getApplication())
            _message.value = "Alert reactivated"
        }
    }

    fun disableAlert(id: Int) {
        viewModelScope.launch {
            repo.disableAlert(id)
            _message.value = "Alert disabled"
        }
    }

    fun refreshNow() {
        viewModelScope.launch {
            _isLoading.value = true
            PriceCheckWorker.runNow(getApplication())
            _isLoading.value = false
            _message.value = "Checking prices…"
        }
    }

    fun clearMessage() { _message.value = null }
}
