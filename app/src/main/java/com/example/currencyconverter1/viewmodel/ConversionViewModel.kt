package com.example.currencyconverter1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter1.data.CurrencyDatabase
import com.example.currencyconverter1.data.entity.ConversionHistory
import com.example.currencyconverter1.repository.ConversionRepository
import com.example.currencyconverter1.utils.PrefManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConversionViewModel(
    private val database: CurrencyDatabase,
    private val prefManager: PrefManager
) : ViewModel() {
    private val repository = ConversionRepository(database)

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    private val _historyState = MutableStateFlow<HistoryState>(HistoryState.Idle)
    val historyState: StateFlow<HistoryState> = _historyState.asStateFlow()

    fun saveConversion(
        fromCurrency: String,
        toCurrency: String,
        amount: Double,
        convertedAmount: Double,
        rate: Double
    ) {
        val userId = prefManager.getCurrentUserId()
        if (userId == -1L) return

        viewModelScope.launch {
            try {
                val conversion = ConversionHistory(
                    userId = userId,
                    fromCurrency = fromCurrency,
                    toCurrency = toCurrency,
                    amount = amount,
                    convertedAmount = convertedAmount,
                    rate = rate
                )
                repository.saveConversion(conversion)
                _saveState.value = SaveState.Success
            } catch (e: Exception) {
                _saveState.value = SaveState.Error("Failed to save conversion: ${e.message}")
            }
        }
    }

    fun loadConversionHistory() {
        val userId = prefManager.getCurrentUserId()
        if (userId == -1L) {
            _historyState.value = HistoryState.Error("User not logged in")
            return
        }

        _historyState.value = HistoryState.Loading
        viewModelScope.launch {
            try {
                repository.getConversionHistory(userId).collect { history ->
                    _historyState.value = HistoryState.Success(history)
                }
            } catch (e: Exception) {
                _historyState.value = HistoryState.Error("Failed to load history: ${e.message}")
            }
        }
    }

    fun clearHistory() {
        val userId = prefManager.getCurrentUserId()
        if (userId == -1L) return

        viewModelScope.launch {
            repository.clearHistory(userId)
            loadConversionHistory()
        }
    }

    sealed class SaveState {
        object Idle : SaveState()
        object Success : SaveState()
        data class Error(val message: String) : SaveState()
    }

    sealed class HistoryState {
        object Idle : HistoryState()
        object Loading : HistoryState()
        data class Success(val history: List<ConversionHistory>) : HistoryState()
        data class Error(val message: String) : HistoryState()
    }
}