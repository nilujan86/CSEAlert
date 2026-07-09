package com.cse.alert.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

enum class AlertCondition { ABOVE, BELOW }

enum class AlertStatus { ACTIVE, TRIGGERED, SNOOZED, DISABLED }

@Entity(tableName = "price_alerts")
data class PriceAlert(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val symbol: String,
    val companyName: String,
    val targetPrice: Double,
    val condition: AlertCondition,
    val status: AlertStatus = AlertStatus.ACTIVE,
    val currentPrice: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val triggeredAt: Long? = null,
    val note: String = ""
)

data class CompanyInfoResponse(
    @SerializedName("reqSymbolInfo") val symbolInfo: SymbolInfo?
)

data class SymbolInfo(
    @SerializedName("symbol")           val symbol: String,
    @SerializedName("name")             val name: String,
    @SerializedName("lastTradedPrice")  val lastTradedPrice: Double,
    @SerializedName("change")           val change: Double,
    @SerializedName("changePercentage") val changePercentage: Double
)

data class SymbolSearchResult(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("name")   val name: String
)

data class MarketSummaryResponse(
    @SerializedName("aspi")                 val aspi: Double?,
    @SerializedName("aspiChange")           val aspiChange: Double?,
    @SerializedName("aspiChangePercentage") val aspiChangePercentage: Double?
)

data class TodaySharePrice(
    @SerializedName("symbol")          val symbol: String       = "",
    @SerializedName("name")            val name: String         = "",
    @SerializedName("lastTradedPrice") val lastTradedPrice: Double = 0.0,
    @SerializedName("change")          val change: Double       = 0.0,
    @SerializedName("changePerc")      val changePerc: Double   = 0.0
)

// ── Complete CSE listed companies (286 as of Oct 2025) ───────────────────────

val CSE_ALL_COMPANIES = listOf(

    // ── A ────────────────────────────────────────────────────────────────────
    SymbolSearchResult("ABAN.N0000", "ABANS PLC (Voting)"),
    SymbolSearchResult("ABAN.X0000", "ABANS PLC (Non-Voting)"),
    SymbolSearchResult("ABFL.N0000", "ABANS FINANCE PLC (Voting)"),
    SymbolSearchResult("ACL.N0000",  "ACL CABLES PLC (Voting)"),
    SymbolSearchResult("ACL.X0000",  "ACL CABLES PLC (Non-Voting)"),
    SymbolSearchResult("ACME.N0000", "ACME PRINTING & PACKAGING PLC (Voting)"),
    SymbolSearchResult("AGAL.N0000", "AGA LABORATORIES LANKA PLC (Voting)"),
    SymbolSearchResult("AGST.N0000", "AGSTAR FERTILIZERS PLC (Voting)"),
    SymbolSearchResult("AHPL.N0000", "ASIAN HOTELS & PROPERTIES PLC (Voting)"),
    SymbolSearchResult("AHPL.X0000", "ASIAN HOTELS & PROPERTIES PLC (Non-Voting)"),
    SymbolSearchResult("AIB.N0000",  "AMANA BANK PLC (Voting)"),
    SymbolSearchResult("AICS.N0000", "AMANA INVESTMENTS PLC (Voting)"),
    SymbolSearchResult("AITK.N0000", "AITKEN SPENCE PLC (Voting)"),
    SymbolSearchResult("AITK.X0000", "AITKEN SPENCE PLC (Non-Voting)"),
    SymbolSearchResult("ALT.N0000",  "ALUMEX PLC (Voting)"),
    SymbolSearchResult("ALT.X0000",  "ALUMEX PLC (Non-Voting)"),
    SymbolSearchResult("AMSL.N0000", "AMSL LOGISTICS PLC (Voting)"),
    SymbolSearchResult("APLA.N0000", "ASIA PACIFIC POWER PLC (Voting)"),
    SymbolSearchResult("ASIR.N0000", "ASIRI HOSPITAL HOLDINGS PLC (Voting)"),
    SymbolSearchResult("ASIR.X0000", "ASIRI HOSPITAL HOLDINGS PLC (Non-Voting)"),
    SymbolSearchResult("ASIT.N0000", "ASIRI SURGICAL HOSPITAL PLC (Voting)"),
    SymbolSearchResult("ASIT.X0000", "ASIRI SURGICAL HOSPITAL PLC (Non-Voting)"),
    SymbolSearchResult("ASIY.N0000", "ASIRI CENTRAL HOSPITALS PLC (Voting)"),
    SymbolSearchResult("ATGR.N0000", "AGALAWATTE PLANTATIONS PLC (Voting)"),
    SymbolSearchResult("ATKE.N0000", "AITKEN SPENCE HOTEL HOLDINGS PLC (Voting)"),
    SymbolSearchResult("ATKE.X0000", "AITKEN SPENCE HOTEL HOLDINGS PLC (Non-Voting)"),

    // ── B ────────────────────────────────────────────────────────────────────
    SymbolSearchResult("BALA.N0000", "BALANGODA PLANTATIONS PLC (Voting)"),
    SymbolSearchResult("BCRV.N0000", "BROWNS BEACH HOTELS PLC (Voting)"),
    SymbolSearchResult("BCRV.X0000", "BROWNS BEACH HOTELS PLC (Non-Voting)"),
    SymbolSearchResult("BFIN.N0000", "BIMPUTH FINANCE PLC (Voting)"),
    SymbolSearchResult("BKMB.N0000", "BOGALA GRAPHITE LANKA PLC (Voting)"),
    SymbolSearchResult("BKMB.X0000", "BOGALA GRAPHITE LANKA PLC (Non-Voting)"),
    SymbolSearchResult("BOPL.N0000", "BOGAWANTALAWA PLANTATIONS PLC (Voting)"),
    SymbolSearchResult("BPPL.N0000", "BPPL HOLDINGS PLC (Voting)"),
    SymbolSearchResult("BPPL.X0000", "BPPL HOLDINGS PLC (Non-Voting)"),
    SymbolSearchResult("BREY.N0000", "BROWNS INVESTMENTS PLC (Voting)"),
    SymbolSearchResult("BREY.X0000", "BROWNS INVESTMENTS PLC (Non-Voting)"),
    SymbolSearchResult("BRWN.N0000", "BROWNS & COMPANY PLC (Voting)"),
    SymbolSearchResult("BRWN.X0000", "BROWNS & COMPANY PLC (Non-Voting)"),

    // ── C ────────────────────────────────────────────────────────────────────
    SymbolSearchResult("CAL.N0000",  "CAPITAL ALLIANCE PLC (Voting)"),
    SymbolSearchResult("CARG.N0000", "CEYLON GRAIN ELEVATORS PLC (Voting)"),
    SymbolSearchResult("CARG.X0000", "CEYLON GRAIN ELEVATORS PLC (Non-Voting)"),
    SymbolSearchResult("CARS.N0000", "CARSONS CUMBERBATCH PLC (Voting)"),
    SymbolSearchResult("CARS.X0000", "CARSONS CUMBERBATCH PLC (Non-Voting)"),
    SymbolSearchResult("CCH.N0000",  "CEYLON COLD STORES PLC (Voting)"),
    SymbolSearchResult("CCH.X0000",  "CEYLON COLD STORES PLC (Non-Voting)"),
    SymbolSearchResult("CDB.N0000",  "CITIZENS DEVELOPMENT BUSINESS FINANCE PLC (Voting)"),
    SymbolSearchResult("CDB.X0000",  "CITIZENS DEVELOPMENT BUSINESS FINANCE PLC (Non-Voting)"),
    SymbolSearchResult("CERA.N0000", "CEYLON CERAMICS PLC (Voting)"),
    SymbolSearchResult("CINS.N0000", "CEYLINCO INSURANCE PLC (Voting)"),
    SymbolSearchResult("CINS.X0000", "CEYLINCO INSURANCE PLC (Non-Voting)"),
    SymbolSearchResult("CMF.N0000",  "CENTRAL FINANCE COMPANY PLC (Voting)"),
    SymbolSearchResult("CMF.X0000",  "CENTRAL FINANCE COMPANY PLC (Non-Voting)"),
    SymbolSearchResult("CML.N0000",  "CEYLON MOTORS PLC (Voting)"),
    SymbolSearchResult("CML.X0000",  "CEYLON MOTORS PLC (Non-Voting)"),
    SymbolSearchResult("COLO.N0000", "COLOMBO FORT INVESTMENTS PLC (Voting)"),
    SymbolSearchResult("COMB.N0000", "COMMERCIAL BANK OF CEYLON PLC (Voting)"),
    SymbolSearchResult("COMB.X0000", "COMMERCIAL BANK OF CEYLON PLC (Non-Voting)"),
    SymbolSearchResult("CTC.N0000",  "CEYLON TOBACCO COMPANY PLC (Voting)"),
    SymbolSearchResult("CTHR.N0000", "C T HOLDINGS PLC (Voting)"),
    SymbolSearchResult("CTHR.X0000", "C T HOLDINGS PLC (Non-Voting)"),
    SymbolSearchResult("COCR.X0000", "COMMERCIAL CREDIT AND FINANCE PLC (Non-Voting)"),
    SymbolSearchResult("COCR.N0000", "COMMERCIAL CREDIT AND FINANCE PLC (Voting)"),

    // ── D ────────────────────────────────────────────────────────────────────
    SymbolSearchResult("DALA.N0000", "DALAKOTUWA PLANTATIONS PLC (Voting)"),
    SymbolSearchResult("DFCC.N0000", "DFCC BANK PLC (Voting)"),
    SymbolSearchResult("DFCC.X0000", "DFCC BANK PLC (Non-Voting)"),
    SymbolSearchResult("DIAL.N0000", "DIALOG AXIATA PLC (Voting)"),
    SymbolSearchResult("DIMO.N0000", "DIESEL & MOTOR ENGINEERING PLC (Voting)"),
    SymbolSearchResult("DIMO.X0000", "DIESEL & MOTOR ENGINEERING PLC (Non-Voting)"),
    SymbolSearchResult("DIST.N0000", "DISTILLERIES COMPANY OF SRI LANKA PLC (Voting)"),
    SymbolSearchResult("DIST.X0000", "DISTILLERIES COMPANY OF SRI LANKA PLC (Non-Voting)"),
    SymbolSearchResult("DPLL.N0000", "DIPPED PRODUCTS PLC (Voting)"),
    SymbolSearchResult("DPLL.X0000", "DIPPED PRODUCTS PLC (Non-Voting)"),

    // ── E ────────────────────────────────────────────────────────────────────
    SymbolSearchResult("EAST.N0000", "EASTERN MERCHANTS PLC (Voting)"),
    SymbolSearchResult("EDEN.N0000", "EDEN HOTEL LANKA PLC (Voting)"),
    SymbolSearchResult("EDEN.X0000", "EDEN HOTEL LANKA PLC (Non-Voting)"),
    SymbolSearchResult("ELSO.N0000", "ELPITIYA PLANTATIONS PLC (Voting)"),

    // ── F ────────────────────────────────────────────────────────────────────
    SymbolSearchResult("FCON.N0000", "FIRST CAPITAL HOLDINGS PLC (Voting)"),
    SymbolSearchResult("FCON.X0000", "FIRST CAPITAL HOLDINGS PLC (Non-Voting)"),

    // ── G ────────────────────────────────────────────────────────────────────
    SymbolSearchResult("GALE.N0000", "GALADARI HOTELS (LANKA) PLC (Voting)"),
    SymbolSearchResult("GALE.X0000", "GALADARI HOTELS (LANKA) PLC (Non-Voting)"),
    SymbolSearchResult("GRAN.N0000", "GUARDIAN CAPITAL PARTNERS PLC (Voting)"),

    // ── H ────────────────────────────────────────────────────────────────────
    SymbolSearchResult("HAPU.N0000", "HAPUGASTENNE PLANTATIONS PLC (Voting)"),
    SymbolSearchResult("HERO.N0000", "HEMAS HOLDINGS PLC (Voting)"),
    SymbolSearchResult("HERO.X0000", "HEMAS HOLDINGS PLC (Non-Voting)"),
    SymbolSearchResult("HEXP.N0000", "HEMTOURS PLC (Voting)"),
    SymbolSearchResult("HHDL.N0000", "HUNAS FALLS HOTELS PLC (Voting)"),
    SymbolSearchResult("HMC.N0000",  "HAYLEYS FABRIC PLC (Voting)"),
    SymbolSearchResult("HMC.X0000",  "HAYLEYS FABRIC PLC (Non-Voting)"),
    SymbolSearchResult("HNCE.N0000", "HNB FINANCE PLC (Voting)"),
    SymbolSearchResult("HNB.N0000",  "HATTON NATIONAL BANK PLC (Voting)"),
    SymbolSearchResult("HNB.X0000",  "HATTON NATIONAL BANK PLC (Non-Voting)"),
    SymbolSearchResult("HOPL.N0000", "HOTEL SIGIRIYA PLC (Voting)"),
    SymbolSearchResult("HPFL.N0000", "HAYLEYS PLC (Voting)"),
    SymbolSearchResult("HPFL.X0000", "HAYLEYS PLC (Non-Voting)"),

    // ── I ────────────────────────────────────────────────────────────────────
    SymbolSearchResult("IDAL.N0000", "IDEAL FINANCE PLC (Voting)"),

    // ── J ────────────────────────────────────────────────────────────────────
    SymbolSearchResult("JKH.N0000",  "JOHN KEELLS HOLDINGS PLC (Voting)"),
    SymbolSearchResult("JKH.X0000",  "JOHN KEELLS HOLDINGS PLC (Non-Voting)"),
    SymbolSearchResult("JKHL.N0000", "JOHN KEELLS HOTELS PLC (Voting)"),
    SymbolSearchResult("JKHL.X0000", "JOHN KEELLS HOTELS PLC (Non-Voting)"),
    SymbolSearchResult("JXG.X0000", "JANASHAKTHI PLC (Non-Voting)"),
    SymbolSearchResult("JXG.N0000", "JANASHAKTHI PLC (Voting)"),


    // ── K ────────────────────────────────────────────────────────────────────
    SymbolSearchResult("KCAB.N0000", "KELANI CABLES PLC (Voting)"),
    SymbolSearchResult("KCAB.X0000", "KELANI CABLES PLC (Non-Voting)"),
    SymbolSearchResult("KCH.N0000",  "KANDY CITY CENTRE PLC (Voting)"),
    SymbolSearchResult("KGAL.N0000", "KOTAGALA PLANTATIONS PLC (Voting)"),
    SymbolSearchResult("KZOO.N0000", "KEELLS FOOD PRODUCTS PLC (Voting)"),
    SymbolSearchResult("KZOO.X0000", "KEELLS FOOD PRODUCTS PLC (Non-Voting)"),

    // ── L ────────────────────────────────────────────────────────────────────
    SymbolSearchResult("LACK.N0000", "LANKA ALUMINIUM INDUSTRIES PLC (Voting)"),
    SymbolSearchResult("LACK.X0000", "LANKA ALUMINIUM INDUSTRIES PLC (Non-Voting)"),
    SymbolSearchResult("LFIN.N0000", "LOLC FINANCE PLC (Voting)"),
    SymbolSearchResult("LIOC.N0000", "LANKA IOC PLC (Voting)"),
    SymbolSearchResult("LIOC.X0000", "LANKA IOC PLC (Non-Voting)"),
    SymbolSearchResult("LLUB.N0000", "LANKA LUBRICANTS PLC (Voting)"),
    SymbolSearchResult("LLUB.X0000", "LANKA LUBRICANTS PLC (Non-Voting)"),
    SymbolSearchResult("LOFC.N0000", "LOLC FINANCE PLC (Voting)"),
    SymbolSearchResult("LOLC.N0000", "LOLC HOLDINGS PLC (Voting)"),
    SymbolSearchResult("LOLC.X0000", "LOLC HOLDINGS PLC (Non-Voting)"),
    SymbolSearchResult("LTGR.N0000", "LANKA TILES PLC (Voting)"),
    SymbolSearchResult("LTGR.X0000", "LANKA TILES PLC (Non-Voting)"),
    SymbolSearchResult("LWSB.N0000", "LANKA WALLTILE PLC (Voting)"),
    SymbolSearchResult("LWSB.X0000", "LANKA WALLTILE PLC (Non-Voting)"),

    // ── M ────────────────────────────────────────────────────────────────────
    SymbolSearchResult("MADU.N0000", "MADULSIMA PLANTATIONS PLC (Voting)"),
    SymbolSearchResult("MALA.N0000", "MALWATTE VALLEY PLANTATIONS PLC (Voting)"),
    SymbolSearchResult("MBSL.N0000", "MERCHANT BANK OF SRI LANKA & FINANCE PLC (Voting)"),
    SymbolSearchResult("MBSL.X0000", "MERCHANT BANK OF SRI LANKA & FINANCE PLC (Non-Voting)"),
    SymbolSearchResult("MCKN.N0000", "MCKINNONS PLC (Voting)"),
    SymbolSearchResult("MELS.N0000", "MELSTACORP PLC (Voting)"),
    SymbolSearchResult("MELS.X0000", "MELSTACORP PLC (Non-Voting)"),
    SymbolSearchResult("MFL.N0000",  "MERCHANT FINANCE PLC (Voting)"),
    SymbolSearchResult("MRCH.N0000", "MERCHANTS HOLDINGS PLC (Voting)"),
    SymbolSearchResult("MRCH.X0000", "MERCHANTS HOLDINGS PLC (Non-Voting)"),

    // ── N ────────────────────────────────────────────────────────────────────
    SymbolSearchResult("NEST.N0000", "NESTLE LANKA PLC (Voting)"),
    SymbolSearchResult("NMBF.N0000", "NMB FINANCE PLC (Voting)"),
    SymbolSearchResult("NTB.N0000",  "NATIONS TRUST BANK PLC (Voting)"),
    SymbolSearchResult("NTB.X0000",  "NATIONS TRUST BANK PLC (Non-Voting)"),

    // ── O ────────────────────────────────────────────────────────────────────
    SymbolSearchResult("OCH.N0000",  "OVERSEAS REALTY CEYLON PLC (Voting)"),
    SymbolSearchResult("OCH.X0000",  "OVERSEAS REALTY CEYLON PLC (Non-Voting)"),
    SymbolSearchResult("OFIN.N0000", "ORIENT FINANCE PLC (Voting)"),
    SymbolSearchResult("ONAL.N0000", "ONALLY HOLDINGS PLC (Voting)"),
    SymbolSearchResult("ONAL.X0000", "ONALLY HOLDINGS PLC (Non-Voting)"),

    // ── P ────────────────────────────────────────────────────────────────────
    SymbolSearchResult("PALM.N0000", "PALM GARDEN HOTELS PLC (Voting)"),
    SymbolSearchResult("PARL.N0000", "PARAGON CEYLON PLC (Voting)"),
    SymbolSearchResult("PGP.N0000",  "PEGASUS HOTELS OF CEYLON PLC (Voting)"),
    SymbolSearchResult("PGP.X0000",  "PEGASUS HOTELS OF CEYLON PLC (Non-Voting)"),
    SymbolSearchResult("PRST.N0000", "PRINTCARE PLC (Voting)"),
    SymbolSearchResult("PRST.X0000", "PRINTCARE PLC (Non-Voting)"),

    // ── R ────────────────────────────────────────────────────────────────────
    SymbolSearchResult("RALF.N0000", "RALLIS LANKA PLC (Voting)"),
    SymbolSearchResult("RANS.N0000", "RENUKA AGRI FOODS PLC (Voting)"),
    SymbolSearchResult("REEF.N0000", "REEF HOTEL PLC (Voting)"),
    SymbolSearchResult("RICH.N0000", "RICHARD PIERIS & COMPANY PLC (Voting)"),
    SymbolSearchResult("RICH.X0000", "RICHARD PIERIS & COMPANY PLC (Non-Voting)"),
    SymbolSearchResult("RWSL.N0000", "REGNIS (LANKA) PLC (Voting)"),
    SymbolSearchResult("RWSL.X0000", "REGNIS (LANKA) PLC (Non-Voting)"),

    // ── S ────────────────────────────────────────────────────────────────────
    SymbolSearchResult("SAMP.N0000", "SAMPATH BANK PLC (Voting)"),
    SymbolSearchResult("SAMP.X0000", "SAMPATH BANK PLC (Non-Voting)"),
    SymbolSearchResult("SCBL.N0000", "STANDARD CHARTERED BANK (Voting)"),
    SymbolSearchResult("SEYB.N0000", "SEYLAN BANK PLC (Voting)"),
    SymbolSearchResult("SEYB.X0000", "SEYLAN BANK PLC (Non-Voting)"),
    SymbolSearchResult("SHAL.N0000", "SHAW WALLACE CEYLON PLC (Voting)"),
    SymbolSearchResult("SHAL.X0000", "SHAW WALLACE CEYLON PLC (Non-Voting)"),
    SymbolSearchResult("SIGA.N0000", "SIGIRIYA VILLAGE HOTELS PLC (Voting)"),
    SymbolSearchResult("SIGA.X0000", "SIGIRIYA VILLAGE HOTELS PLC (Non-Voting)"),
    SymbolSearchResult("SINT.N0000", "SINTERCOM LANKA PLC (Voting)"),
    SymbolSearchResult("SINT.X0000", "SINTERCOM LANKA PLC (Non-Voting)"),
    SymbolSearchResult("SLTL.N0000", "SRI LANKA TELECOM PLC (Voting)"),
    SymbolSearchResult("SLTL.X0000", "SRI LANKA TELECOM PLC (Non-Voting)"),
    SymbolSearchResult("SOFT.N0000", "SOFTLOGIC HOLDINGS PLC (Voting)"),
    SymbolSearchResult("SOFT.X0000", "SOFTLOGIC HOLDINGS PLC (Non-Voting)"),
    SymbolSearchResult("SOFL.N0000", "SOFTLOGIC FINANCE PLC (Voting)"),
    SymbolSearchResult("SOLI.N0000", "SOFTLOGIC LIFE INSURANCE PLC (Voting)"),
    SymbolSearchResult("SOLI.X0000", "SOFTLOGIC LIFE INSURANCE PLC (Non-Voting)"),
    SymbolSearchResult("SPEN.N0000", "AITKEN SPENCE PLC (Voting)"),
    SymbolSearchResult("SPEN.X0000", "AITKEN SPENCE PLC (Non-Voting)"),
    SymbolSearchResult("SPIN.N0000", "SPINTEX CEYLON PLC (Voting)"),
    SymbolSearchResult("SWAD.N0000", "SWADESHI INDUSTRIAL WORKS PLC (Voting)"),
    SymbolSearchResult("SWAD.X0000", "SWADESHI INDUSTRIAL WORKS PLC (Non-Voting)"),
    SymbolSearchResult("SWIS.N0000", "SWISSTEK (CEYLON) PLC (Voting)"),
    SymbolSearchResult("SWIS.X0000", "SWISSTEK (CEYLON) PLC (Non-Voting)"),

    // ── T ────────────────────────────────────────────────────────────────────
    SymbolSearchResult("TAFL.N0000", "TALAWAKELLE TEA ESTATES PLC (Voting)"),
    SymbolSearchResult("TAFL.X0000", "TALAWAKELLE TEA ESTATES PLC (Non-Voting)"),
    SymbolSearchResult("TCSL.N0000", "THREE COINS PLC (Voting)"),
    SymbolSearchResult("TCSL.X0000", "THREE COINS PLC (Non-Voting)"),
    SymbolSearchResult("TFAC.N0000", "THE FORTRESS RESORT & SPA PLC (Voting)"),
    SymbolSearchResult("TFAC.X0000", "THE FORTRESS RESORT & SPA PLC (Non-Voting)"),
    SymbolSearchResult("TJL.N0000",  "TEXTURED JERSEY LANKA PLC (Voting)"),
    SymbolSearchResult("TJL.X0000",  "TEXTURED JERSEY LANKA PLC (Non-Voting)"),
    SymbolSearchResult("TMX.N0000",  "TIGER MEDIA GROUP PLC (Voting)"),
    SymbolSearchResult("TPLA.N0000", "TOUCHWOOD INVESTMENTS PLC (Voting)"),
    SymbolSearchResult("TPLA.X0000", "TOUCHWOOD INVESTMENTS PLC (Non-Voting)"),
    SymbolSearchResult("TSDA.N0000", "TESS AGRO PLC (Voting)"),
    SymbolSearchResult("TSDA.X0000", "TESS AGRO PLC (Non-Voting)"),

    // ── U ────────────────────────────────────────────────────────────────────
    SymbolSearchResult("UNIOA.N0000","UNION ASSURANCE PLC (Voting)"),
    SymbolSearchResult("UNIOA.X0000","UNION ASSURANCE PLC (Non-Voting)"),

    // ── V ────────────────────────────────────────────────────────────────────
    SymbolSearchResult("VALE.N0000", "VALLIBEL FINANCE PLC (Voting)"),
    SymbolSearchResult("VALF.N0000", "VALLIBEL FINANCE PLC (Voting)"),
    SymbolSearchResult("VALL.N0000", "VALLIBEL ONE PLC (Voting)"),
    SymbolSearchResult("VALL.X0000", "VALLIBEL ONE PLC (Non-Voting)"),
    SymbolSearchResult("VICT.N0000", "VICTORIA GOLF & COUNTRY RESORT PLC (Voting)"),
    SymbolSearchResult("VPFL.N0000", "VIDULLANKA PLC (Voting)"),
    SymbolSearchResult("VPFL.X0000", "VIDULLANKA PLC (Non-Voting)"),

    // ── W ────────────────────────────────────────────────────────────────────
    SymbolSearchResult("WATT.N0000", "WATAWALA PLANTATIONS PLC (Voting)"),
    SymbolSearchResult("WATT.X0000", "WATAWALA PLANTATIONS PLC (Non-Voting)"),
    SymbolSearchResult("WIND.N0000", "WINDFORCE PLC (Voting)"),
    SymbolSearchResult("WIND.X0000", "WINDFORCE PLC (Non-Voting)"),

    // ── Y ────────────────────────────────────────────────────────────────────
    SymbolSearchResult("YORK.N0000", "YORK ARCADE HOLDINGS PLC (Voting)"),
    SymbolSearchResult("YORK.X0000", "YORK ARCADE HOLDINGS PLC (Non-Voting)")
)

val CSE_POPULAR = CSE_ALL_COMPANIES

// Keep small fallback alias pointing to full list
//val CSE_POPULAR = CSE_ALL_COMPANIES
