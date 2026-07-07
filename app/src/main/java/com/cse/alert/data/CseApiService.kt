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

    /** Returns all ~286 listed companies — POST with no body required */
    @FormUrlEncoded
    @POST("todaySharePrice")
    suspend fun getTodaySharePrice(
        @Field("key") key: String = ""
    ): Response<List<TodaySharePrice>>
}
