package com.cse.alert.data

import com.cse.alert.model.CompanyInfoResponse
import com.cse.alert.model.MarketSummaryResponse
import com.cse.alert.model.SymbolSearchResult
import com.cse.alert.model.TodaySharePrice
import retrofit2.Response
import retrofit2.http.*

interface CseApiService {

    @FormUrlEncoded
    @POST("companyInfoSummery")
    suspend fun getCompanyInfo(
        @Field("symbol") symbol: String
    ): Response<CompanyInfoResponse>

    @GET("marketSummary")
    suspend fun getMarketSummary(): Response<MarketSummaryResponse>

    @FormUrlEncoded
    @POST("symbolSearch")
    suspend fun searchSymbols(
        @Field("keyword") keyword: String
    ): Response<List<SymbolSearchResult>>

    /** Returns ALL listed companies with today's prices — single POST, no params needed */
    @POST("todaySharePrice")
    suspend fun getTodaySharePrice(): Response<List<TodaySharePrice>>
}
