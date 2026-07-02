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
            val response = if (query.isBlank()) {
                api.getAllSymbols()
            } else {
                api.searchSymbols(query)
            }
            if (response.isSuccessful) {
                val results = response.body()?.filter { it.symbol.isNotEmpty() } ?: emptyList()
                if (results.isEmpty()) fallback(query) else results
            } else fallback(query)
        } catch (e: Exception) {
            fallback(query)
        }
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
