package com.cse.alert.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

// ── Alert condition types ─────────────────────────────────────────────────────

enum class AlertCondition {
    ABOVE,   // notify when price goes ABOVE target
    BELOW    // notify when price goes BELOW target
}

enum class AlertStatus {
    ACTIVE,     // watching
    TRIGGERED,  // condition met, notification sent
    SNOOZED,    // user snoozed it
    DISABLED    // user turned it off
}

// ── Room entity ───────────────────────────────────────────────────────────────

@Entity(tableName = "price_alerts")
data class PriceAlert(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val symbol: String,
    val companyName: String,
    val targetPrice: Double,
    val condition: AlertCondition,      // ABOVE or BELOW
    val status: AlertStatus = AlertStatus.ACTIVE,
    val currentPrice: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val triggeredAt: Long? = null,
    val note: String = ""               // optional user note e.g. "Buy signal"
)

// ── API response models ───────────────────────────────────────────────────────

data class CompanyInfoResponse(
    @SerializedName("reqSymbolInfo") val symbolInfo: SymbolInfo?
)

data class SymbolInfo(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("name") val name: String,
    @SerializedName("lastTradedPrice") val lastTradedPrice: Double,
    @SerializedName("change") val change: Double,
    @SerializedName("changePercentage") val changePercentage: Double
)

data class SymbolSearchResult(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("name") val name: String
)

data class MarketSummaryResponse(
    @SerializedName("aspi") val aspi: Double?,
    @SerializedName("aspiChange") val aspiChange: Double?,
    @SerializedName("aspiChangePercentage") val aspiChangePercentage: Double?
)

// ── UI state ──────────────────────────────────────────────────────────────────

data class AlertUiState(
    val alerts: List<PriceAlert> = emptyList(),
    val isLoading: Boolean = false,
    val message: String? = null,
    val searchResults: List<SymbolSearchResult> = emptyList()
)
data class TodaySharePrice(
    @SerializedName("symbol")          val symbol: String,
    @SerializedName("name")            val name: String,
    @SerializedName("lastTradedPrice") val lastTradedPrice: Double,
    @SerializedName("change")          val change: Double,
    @SerializedName("changePerc")      val changePerc: Double
)
// ── Popular CSE stocks fallback ───────────────────────────────────────────────

val CSE_POPULAR = listOf(
    SymbolSearchResult("LOLC.N0000",  "LOLC Holdings PLC"),
    SymbolSearchResult("JKH.N0000",   "John Keells Holdings PLC"),
    SymbolSearchResult("COMB.N0000",  "Commercial Bank of Ceylon PLC"),
    SymbolSearchResult("HNB.N0000",   "Hatton National Bank PLC"),
    SymbolSearchResult("DIAL.N0000",  "Dialog Axiata PLC"),
    SymbolSearchResult("SAMP.N0000",  "Sampath Bank PLC"),
    SymbolSearchResult("LIOC.N0000",  "Lanka IOC PLC"),
    SymbolSearchResult("NTB.N0000",   "Nations Trust Bank PLC"),
    SymbolSearchResult("RICH.N0000",  "Richard Pieris & Company PLC"),
    SymbolSearchResult("DIST.N0000",  "Distilleries Company of Sri Lanka PLC"),
    SymbolSearchResult("CTC.N0000",   "Ceylon Tobacco Company PLC"),
    SymbolSearchResult("TJL.N0000",   "Textured Jersey Lanka PLC"),
    SymbolSearchResult("AHPL.N0000",  "Asian Hotels & Properties PLC"),
    SymbolSearchResult("GRAN.N0000",  "Guardian Capital Partners PLC"),
    SymbolSearchResult("CINS.N0000",  "Ceylinco Insurance PLC")
)
