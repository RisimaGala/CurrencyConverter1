package com.example.currencyconverter1.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.currencyconverter1.data.entity.ConversionHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversionDao {
    @Insert
    suspend fun insertConversion(conversion: ConversionHistory)

    @Query("SELECT * FROM conversion_history WHERE userId = :userId ORDER BY timestamp DESC")
    fun getConversionHistory(userId: Long): Flow<List<ConversionHistory>>

    @Query("DELETE FROM conversion_history WHERE userId = :userId")
    suspend fun clearUserHistory(userId: Long)
}