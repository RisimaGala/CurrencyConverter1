package com.example.currencyconverter1.repository

import com.example.currencyconverter1.data.CurrencyDatabase
import com.example.currencyconverter1.data.entity.ConversionHistory
import kotlinx.coroutines.flow.Flow

class ConversionRepository(private val database: CurrencyDatabase) {
    private val conversionDao = database.conversionDao()

    suspend fun saveConversion(conversion: ConversionHistory) {
        conversionDao.insertConversion(conversion)
    }

    fun getConversionHistory(userId: Long): Flow<List<ConversionHistory>> {
        return conversionDao.getConversionHistory(userId)
    }

    suspend fun clearHistory(userId: Long) {
        conversionDao.clearUserHistory(userId)
    }
}