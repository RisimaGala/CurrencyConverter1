package com.example.currencyconverter1.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {
    @GET("latest")
    suspend fun getLatestRates(
        @Query("apikey") apiKey: String = "fca_live_gr44zril1pr8rMPViwQclnFQZyptbsIwJRjaDAjF",
        @Query("base_currency") baseCurrency: String? = null
    ): Response<CurrencyResponse>
}

data class CurrencyResponse(
    val data: Map<String, Double>
)