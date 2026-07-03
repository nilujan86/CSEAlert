package com.cse.alert.data

import android.content.Context
import com.cse.alert.model.*
import kotlinx.coroutines.flow.Flow

class AlertRepository(context: Context) {

    private val dao = AlertDatabase.getInstance(context).alertDao()
    private val api = NetworkClient.apiService

    // ── Alerts CRUD ───────────────────────────────────────────────────────────

    val allAlerts: Flow<List<PriceAlert>> = dao.getAllAlerts()

    suspend fun addAlert(alert: PriceAlert): Long = dao.insert(alert)

    suspend fun updateAlert(alert: PriceAlert) = dao.update(alert)

    suspend fun deleteAlert(id: Int) = dao.deleteById(id)

    suspend fun reactivateAlert(id: Int) = dao.reactivate(id)

    suspend fun disableAlert(id: Int) = dao.disable(id)

    suspend fun getActiveAlerts(): List<PriceAlert> = dao.getActiveAlerts()

    // ── Price checking ────────────────────────────────────────────────────────

    /**
     * Fetches current price for [symbol].
     * Returns the price or null on failure.
     */
    suspend fun fetchCurrentPrice(symbol: String): Double? {
        return try {
            val response = api.getCompanyInfo(symbol)
            if (response.isSuccessful) {
                response.body()?.symbolInfo?.lastTradedPrice
            } else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Checks all ACTIVE alerts against live prices.
     * Returns list of alerts that were just triggered.
     */
    suspend fun checkAllAlerts(): List<PriceAlert> {
        val active = dao.getActiveAlerts()
        val triggered = mutableListOf<PriceAlert>()

        for (alert in active) {
            val price = fetchCurrentPrice(alert.symbol) ?: continue

            // Update stored current price
            dao.updateCurrentPrice(alert.id, price)

            val conditionMet = when (alert.condition) {
                AlertCondition.ABOVE -> price >= alert.targetPrice
                AlertCondition.BELOW -> price <= alert.targetPrice
            }

            if (conditionMet) {
                dao.updateStatus(
                    id = alert.id,
                    status = AlertStatus.TRIGGERED,
                    ts = System.currentTimeMillis(),
                    price = price
                )
                triggered.add(alert.copy(currentPrice = price, status = AlertStatus.TRIGGERED))
            }
        }

        return triggered
    }

    // ── Symbol search ─────────────────────────────────────────────────────────

suspend fun searchSymbols(query: String): List<SymbolSearchResult> {
    return try {
        if (query.isBlank()) {
            // Try allSymbols endpoint first
            val response = api.getAllSymbols()
            if (response.isSuccessful) {
                val results = response.body()?.filter { it.symbol.isNotEmpty() } ?: emptyList()
                if (results.isNotEmpty()) return results
            }
            // Fall back to searching with common letters to get more results
            fetchByMultipleKeywords()
        } else {
            val response = api.searchSymbols(query)
            if (response.isSuccessful) {
                val results = response.body()?.filter { it.symbol.isNotEmpty() } ?: emptyList()
                if (results.isNotEmpty()) results else fallback(query)
            } else fallback(query)
        }
    } catch (e: Exception) {
        if (query.isBlank()) fetchByMultipleKeywords() else fallback(query)
    }
}

/**
 * CSE symbol search returns results per keyword.
 * We search A–Z to harvest as many listed companies as possible.
 */
private suspend fun fetchByMultipleKeywords(): List<SymbolSearchResult> {
    val all = mutableMapOf<String, SymbolSearchResult>()

    // Search every letter of the alphabet
    val keywords = ('A'..'Z').map { it.toString() } +
                   listOf("PLC", "BANK", "HOTEL", "FUND", "CEYLON",
                          "LANKA", "SRI", "NATIONAL", "EASTERN", "WESTERN",
                          "CAPITAL", "FINANCE", "INSURANCE", "POWER", "GAS")

    for (keyword in keywords) {
        try {
            val response = api.searchSymbols(keyword)
            if (response.isSuccessful) {
                response.body()
                    ?.filter { it.symbol.isNotEmpty() }
                    ?.forEach { all[it.symbol] = it }
            }
        } catch (e: Exception) {
            // Skip failed keyword, continue with next
        }
    }

    return if (all.isNotEmpty()) {
        all.values.sortedBy { it.name }
    } else {
        CSE_POPULAR
    }
}

private fun fallback(query: String) = if (query.isBlank()) CSE_POPULAR
else CSE_POPULAR.filter {
    it.symbol.contains(query, ignoreCase = true) ||
    it.name.contains(query, ignoreCase = true)
}

    suspend fun fetchCompanyInfo(symbol: String): SymbolInfo? {
        return try {
            val r = api.getCompanyInfo(symbol)
            if (r.isSuccessful) r.body()?.symbolInfo else null
        } catch (e: Exception) { null }
    }

    private fun fallback(query: String) = if (query.isBlank()) CSE_POPULAR
    else CSE_POPULAR.filter {
        it.symbol.contains(query, ignoreCase = true) ||
        it.name.contains(query, ignoreCase = true)
    }
}
