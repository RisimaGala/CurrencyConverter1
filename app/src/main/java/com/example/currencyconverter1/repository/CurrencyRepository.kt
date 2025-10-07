package com.example.currencyconverter1.repository

import android.util.Log
import com.example.currencyconverter1.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CurrencyRepository {
    private val api = ApiClient.instance
    private val TAG = "CurrencyRepository"

    // Static fallback rates in case API fails - INCLUDING ZAR
    private val fallbackRates = mapOf(
        "USD" to 1.0,
        "EUR" to 0.85,
        "GBP" to 0.73,
        "JPY" to 110.0,
        "CAD" to 1.25,
        "AUD" to 1.35,
        "CHF" to 0.92,
        "CNY" to 6.45,
        "INR" to 75.0,
        "SGD" to 1.34,
        "ZAR" to 18.50  // Added South African Rand
    )

    suspend fun getExchangeRates(baseCurrency: String = "USD"): Result<Map<String, Double>> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching rates for base: $baseCurrency")

                val response = api.getLatestRates(baseCurrency = baseCurrency)
                Log.d(TAG, "API Response code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val rates = response.body()!!.data
                    if (rates.isNotEmpty()) {
                        Log.d(TAG, "API Success - Received ${rates.size} rates")
                        Result.success(rates)
                    } else {
                        Log.w(TAG, "API returned empty data, using fallback rates")
                        Result.success(fallbackRates)
                    }
                } else {
                    Log.w(TAG, "API call failed, using fallback rates")
                    Result.success(fallbackRates)
                }
            } catch (e: Exception) {
                Log.e(TAG, "API Error: ${e.message}, using fallback rates")
                Result.success(fallbackRates)
            }
        }
    }

    suspend fun convertCurrency(amount: Double, fromCurrency: String, toCurrency: String): Result<Double> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Converting $amount $fromCurrency to $toCurrency")

                if (fromCurrency == toCurrency) {
                    return@withContext Result.success(amount)
                }

                val ratesResult = getExchangeRates(fromCurrency)
                if (ratesResult.isSuccess) {
                    val rates = ratesResult.getOrNull() ?: fallbackRates
                    val rate = rates[toCurrency]

                    if (rate != null) {
                        val convertedAmount = amount * rate
                        Log.d(TAG, "Conversion successful: $amount $fromCurrency = $convertedAmount $toCurrency")
                        Result.success(convertedAmount)
                    } else {
                        // Calculate via USD if direct rate not available
                        val usdRateFrom = rates["USD"] ?: 1.0
                        val usdAmount = amount / usdRateFrom

                        val ratesTo = getExchangeRates("USD").getOrNull() ?: fallbackRates
                        val usdRateTo = ratesTo[toCurrency] ?: fallbackRates[toCurrency] ?: 1.0

                        val convertedAmount = usdAmount * usdRateTo
                        Log.d(TAG, "Conversion via USD: $amount $fromCurrency = $convertedAmount $toCurrency")
                        Result.success(convertedAmount)
                    }
                } else {
                    // Use fallback rates
                    val fromRate = fallbackRates[fromCurrency] ?: 1.0
                    val toRate = fallbackRates[toCurrency] ?: 1.0
                    val convertedAmount = (amount / fromRate) * toRate
                    Log.d(TAG, "Used fallback rates for conversion")
                    Result.success(convertedAmount)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Conversion error: ${e.message}")
                // Use fallback calculation
                val fromRate = fallbackRates[fromCurrency] ?: 1.0
                val toRate = fallbackRates[toCurrency] ?: 1.0
                val convertedAmount = (amount / fromRate) * toRate
                Result.success(convertedAmount)
            }
        }
    }

    suspend fun getSupportedCurrencies(): List<String> {
        return listOf(
            "USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", "INR", "SGD", "ZAR"
        )
    }

    suspend fun testApiConnection(): Boolean {
        return try {
            val result = getExchangeRates("USD")
            result.isSuccess && result.getOrNull()?.isNotEmpty() == true
        } catch (e: Exception) {
            false
        }
    }
}