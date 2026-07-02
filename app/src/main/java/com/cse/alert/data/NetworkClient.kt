package com.cse.alert.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {

    private const val BASE_URL = "https://www.cse.lk/api/"

    val apiService: CseApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val req = chain.request().newBuilder()
                    .addHeader("Accept", "application/json, text/plain, */*")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("Origin", "https://www.cse.lk")
                    .addHeader("Referer", "https://www.cse.lk/")
                    .addHeader("User-Agent",
                        "Mozilla/5.0 (Linux; Android 16) AppleWebKit/537.36 Chrome/124 Safari/537.36")
                    .build()
                chain.proceed(req)
            }
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CseApiService::class.java)
    }
}
