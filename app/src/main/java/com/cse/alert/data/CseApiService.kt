package com.cse.alert.data

import com.cse.alert.model.CompanyInfoResponse
import com.cse.alert.model.MarketSummaryResponse
import com.cse.alert.model.SymbolSearchResult
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

    @GET("allSymbols")
    suspend fun getAllSymbols(): Response<List<SymbolSearchResult>>
}
