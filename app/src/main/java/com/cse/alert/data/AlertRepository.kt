package com.cse.alert.data

import android.content.Context
import android.util.Log
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

    suspend fun checkAllAlerts(): List<PriceAlert> {
        val active = dao.getActiveAlerts()
        val triggered = mutableListOf<PriceAlert>()

        for (alert in active) {
            val price = fetchCurrentPrice(alert.symbol) ?: continue
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
                triggered.add(
                    alert.copy(currentPrice = price, status = AlertStatus.TRIGGERED)
                )
            }
        }

        return triggered
    }

    // ── Symbol search ─────────────────────────────────────────────────────────

suspend fun searchSymbols(query: String): List<SymbolSearchResult> {
    // Try live API first, merge with static list so nothing is ever missing
    val liveList = fetchAllCompanies()
    val merged = (liveList + CSE_ALL_COMPANIES)
        .distinctBy { it.symbol }
        .sortedBy { it.name }

    return if (query.isBlank()) merged
    else merged.filter {
        it.symbol.contains(query, ignoreCase = true) ||
        it.name.contains(query, ignoreCase = true)
    }
}

    private suspend fun fetchAllCompanies(): List<SymbolSearchResult> {
    return try {
        val response = api.getTodaySharePrice()
        if (response.isSuccessful) {
            response.body()
                ?.filter { it.symbol.isNotEmpty() }
                ?.map { SymbolSearchResult(symbol = it.symbol, name = it.name) }
                ?: emptyList()
        } else emptyList()
    } catch (e: Exception) {
        emptyList()
    }
}

    /** Secondary fallback — searches common prefixes to get broader coverage */
    private suspend fun fetchViaSearch(): List<SymbolSearchResult> {
        val all = mutableMapOf<String, SymbolSearchResult>()
        val keywords = listOf(
            "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P",
            "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
        )
        for (kw in keywords) {
            try {
                val r = api.searchSymbols(kw)
                if (r.isSuccessful) {
                    r.body()
                        ?.filter { it.symbol.isNotEmpty() }
                        ?.forEach { all[it.symbol] = it }
                }
            } catch (e: Exception) { /* skip */ }
        }
        return if (all.isNotEmpty()) all.values.sortedBy { it.name }
        else CSE_POPULAR
    }

    private fun fallback(query: String): List<SymbolSearchResult> {
        return if (query.isBlank()) CSE_POPULAR
        else CSE_POPULAR.filter {
            it.symbol.contains(query, ignoreCase = true) ||
            it.name.contains(query, ignoreCase = true)
        }
    }

    suspend fun fetchCompanyInfo(symbol: String): SymbolInfo? {
        return try {
            val r = api.getCompanyInfo(symbol)
            if (r.isSuccessful) r.body()?.symbolInfo else null
        } catch (e: Exception) { null }
    }
}
