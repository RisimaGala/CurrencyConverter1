package com.example.currencyconverter1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.currencyconverter1.data.CurrencyDatabase
import com.example.currencyconverter1.utils.PrefManager

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val database: CurrencyDatabase,
    private val prefManager: PrefManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UserViewModel::class.java) -> {
                UserViewModel(database, prefManager) as T
            }
            modelClass.isAssignableFrom(ConversionViewModel::class.java) -> {
                ConversionViewModel(database, prefManager) as T
            }
            modelClass.isAssignableFrom(CurrencyViewModel::class.java) -> {
                CurrencyViewModel() as T
            }
            else -> {
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}