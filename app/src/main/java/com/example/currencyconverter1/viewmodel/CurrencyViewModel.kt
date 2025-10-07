package com.example.currencyconverter1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter1.repository.CurrencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CurrencyViewModel : ViewModel() {
    private val repository = CurrencyRepository()

    private val _ratesState = MutableStateFlow<RatesState>(RatesState.Idle)
    val ratesState: StateFlow<RatesState> = _ratesState.asStateFlow()

    private val _conversionState = MutableStateFlow<ConversionState>(ConversionState.Idle)
    val conversionState: StateFlow<ConversionState> = _conversionState.asStateFlow()

    private val _currencies = MutableStateFlow<List<String>>(emptyList())
    val currencies: StateFlow<List<String>> = _currencies.asStateFlow()

    private val _apiStatus = MutableStateFlow<ApiStatus>(ApiStatus.Checking)
    val apiStatus: StateFlow<ApiStatus> = _apiStatus.asStateFlow()

    init {
        loadSupportedCurrencies()
        checkApiStatus()
    }

    fun getExchangeRates(baseCurrency: String = "USD") {
        _ratesState.value = RatesState.Loading
        viewModelScope.launch {
            val result = repository.getExchangeRates(baseCurrency)
            if (result.isSuccess) {
                _ratesState.value = RatesState.Success(result.getOrNull() ?: emptyMap())
            } else {
                _ratesState.value = RatesState.Error("Failed to load rates")
            }
        }
    }

    fun convertCurrency(amount: Double, fromCurrency: String, toCurrency: String) {
        _conversionState.value = ConversionState.Loading
        viewModelScope.launch {
            val result = repository.convertCurrency(amount, fromCurrency, toCurrency)
            if (result.isSuccess) {
                _conversionState.value = ConversionState.Success(result.getOrNull() ?: 0.0)
            } else {
                _conversionState.value = ConversionState.Error("Conversion failed")
            }
        }
    }

    private fun loadSupportedCurrencies() {
        viewModelScope.launch {
            _currencies.value = repository.getSupportedCurrencies()
        }
    }

    private fun checkApiStatus() {
        viewModelScope.launch {
            val isConnected = repository.testApiConnection()
            _apiStatus.value = if (isConnected) ApiStatus.Connected else ApiStatus.Fallback
        }
    }

    sealed class RatesState {
        object Idle : RatesState()
        object Loading : RatesState()
        data class Success(val rates: Map<String, Double>) : RatesState()
        data class Error(val message: String) : RatesState()
    }

    sealed class ConversionState {
        object Idle : ConversionState()
        object Loading : ConversionState()
        data class Success(val amount: Double) : ConversionState()
        data class Error(val message: String) : ConversionState()
    }

    sealed class ApiStatus {
        object Checking : ApiStatus()
        object Connected : ApiStatus()
        object Fallback : ApiStatus()
    }
}