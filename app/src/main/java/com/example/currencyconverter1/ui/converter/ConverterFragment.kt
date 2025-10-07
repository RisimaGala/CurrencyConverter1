package com.example.currencyconverter1.ui.converter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.currencyconverter1.R
import com.example.currencyconverter1.data.CurrencyDatabase
import com.example.currencyconverter1.databinding.FragmentConverterBinding
import com.example.currencyconverter1.utils.PrefManager
import com.example.currencyconverter1.viewmodel.ConversionViewModel
import com.example.currencyconverter1.viewmodel.CurrencyViewModel
import com.example.currencyconverter1.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ConverterFragment : Fragment() {
    private var _binding: FragmentConverterBinding? = null
    private val binding get() = _binding!!
    private val prefManager by lazy { PrefManager.getInstance(requireContext()) }
    private val currencyViewModel: CurrencyViewModel by viewModels()
    private val conversionViewModel: ConversionViewModel by viewModels {
        ViewModelFactory(CurrencyDatabase.getInstance(requireContext()), prefManager)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConverterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!prefManager.isUserLoggedIn()) {
            navigateToLogin()
            return
        }

        setupUI()
        setupObservers()
        setupClickListeners()

        // Load initial rates
        currencyViewModel.getExchangeRates("USD")
    }

    private fun setupUI() {
        // Set up currency spinners
        lifecycleScope.launch {
            currencyViewModel.currencies.collect { currencies ->
                if (currencies.isNotEmpty()) {
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencies)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.fromCurrencySpinner.adapter = adapter
                    binding.toCurrencySpinner.adapter = adapter

                    // Set default selections
                    val usdIndex = currencies.indexOf("USD")
                    val eurIndex = currencies.indexOf("EUR")
                    if (usdIndex != -1) binding.fromCurrencySpinner.setSelection(usdIndex)
                    if (eurIndex != -1) binding.toCurrencySpinner.setSelection(eurIndex)
                }
            }
        }

        val user = prefManager.getCurrentUser()
        binding.welcomeText.text = "Welcome, ${user.name}"
    }

    private fun setupObservers() {
        // Observe API status
        lifecycleScope.launch {
            currencyViewModel.apiStatus.collect { status ->
                when (status) {
                    is CurrencyViewModel.ApiStatus.Connected -> {
                        binding.apiStatusText.visibility = View.VISIBLE
                        binding.apiStatusText.text = "✅ Live Rates Connected"
                        binding.apiStatusText.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.secondary)) // Green color
                    }
                    is CurrencyViewModel.ApiStatus.Fallback -> {
                        binding.apiStatusText.visibility = View.VISIBLE
                        binding.apiStatusText.text = "⚠️ Using Fallback Rates"
                        binding.apiStatusText.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.accent)) // Orange color
                    }
                    else -> {
                        binding.apiStatusText.visibility = View.GONE
                    }
                }
            }
        }

        // Observe rates loading state and display live rates
        lifecycleScope.launch {
            currencyViewModel.ratesState.collect { state ->
                when (state) {
                    is CurrencyViewModel.RatesState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.ratesProgressBar.visibility = View.VISIBLE
                        binding.liveRatesContainer.visibility = View.GONE
                    }
                    is CurrencyViewModel.RatesState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.ratesProgressBar.visibility = View.GONE
                        binding.liveRatesContainer.visibility = View.VISIBLE
                        displayLiveRates(state.rates)
                    }
                    is CurrencyViewModel.RatesState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.ratesProgressBar.visibility = View.GONE
                        binding.liveRatesContainer.visibility = View.VISIBLE
                        Toast.makeText(requireContext(), "Rates error: ${state.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }

        // Observe conversion results
        lifecycleScope.launch {
            currencyViewModel.conversionState.collect { state ->
                when (state) {
                    is CurrencyViewModel.ConversionState.Loading -> {
                        showConversionLoading(true)
                    }
                    is CurrencyViewModel.ConversionState.Success -> {
                        showConversionLoading(false)
                        val convertedAmount = state.amount
                        displayConversionResult(convertedAmount)
                    }
                    is CurrencyViewModel.ConversionState.Error -> {
                        showConversionLoading(false)
                        Toast.makeText(requireContext(), "Conversion failed", Toast.LENGTH_SHORT).show()
                        binding.resultText.text = "Conversion failed"
                    }
                    else -> {}
                }
            }
        }
    }

    private fun displayLiveRates(rates: Map<String, Double>) {
        // Display major currency rates vs USD including ZAR
        updateRateText(binding.eurRateText, "🇪🇺 EUR", rates["EUR"])
        updateRateText(binding.gbpRateText, "🇬🇧 GBP", rates["GBP"])
        updateRateText(binding.jpyRateText, "🇯🇵 JPY", rates["JPY"])
        updateRateText(binding.cadRateText, "🇨🇦 CAD", rates["CAD"])
        updateRateText(binding.audRateText, "🇦🇺 AUD", rates["AUD"])
        updateRateText(binding.zarRateText, "🇿🇦 ZAR", rates["ZAR"])

        binding.liveRatesContainer.visibility = View.VISIBLE
    }

    private fun updateRateText(textView: android.widget.TextView, currencyName: String, rate: Double?) {
        val rateText = if (rate != null) {
            "$currencyName: ${formatRate(rate)}"
        } else {
            "$currencyName: N/A"
        }
        textView.text = rateText
    }

    private fun formatRate(rate: Double): String {
        return if (rate >= 1.0) {
            "%.4f".format(rate)
        } else {
            "%.6f".format(rate)
        }
    }

    private fun setupClickListeners() {
        binding.convertButton.setOnClickListener {
            convertCurrency()
        }

        binding.historyButton.setOnClickListener {
            navigateToHistory()
        }

        binding.logoutButton.setOnClickListener {
            logout()
        }

        binding.swapButton.setOnClickListener {
            swapCurrencies()
        }

        // Add this to your ConverterFragment.kt in setupClickListeners()
        binding.profileButton.setOnClickListener {
            navigateToProfile()
        }

        // Auto-convert when amount changes
        binding.amountEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                convertCurrency()
            }
        }
    }

    private fun convertCurrency() {
        val amountText = binding.amountEditText.text.toString().trim()
        if (amountText.isEmpty()) {
            binding.resultText.text = "Enter amount to convert"
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            binding.resultText.text = "Please enter valid amount"
            return
        }

        val fromCurrency = binding.fromCurrencySpinner.selectedItem.toString()
        val toCurrency = binding.toCurrencySpinner.selectedItem.toString()

        if (fromCurrency == toCurrency) {
            binding.resultText.text = "Same currency: ${"%.2f".format(amount)} $toCurrency"
            return
        }

        currencyViewModel.convertCurrency(amount, fromCurrency, toCurrency)
    }

    private fun displayConversionResult(convertedAmount: Double) {
        val fromCurrency = binding.fromCurrencySpinner.selectedItem.toString()
        val toCurrency = binding.toCurrencySpinner.selectedItem.toString()
        val amount = binding.amountEditText.text.toString().toDoubleOrNull() ?: 0.0

        val resultText = "${"%.2f".format(amount)} $fromCurrency = ${"%.2f".format(convertedAmount)} $toCurrency"
        binding.resultText.text = resultText

        // Save to history
        if (amount > 0) {
            val rate = convertedAmount / amount
            conversionViewModel.saveConversion(fromCurrency, toCurrency, amount, convertedAmount, rate)
        }
    }

    private fun swapCurrencies() {
        val fromPosition = binding.fromCurrencySpinner.selectedItemPosition
        val toPosition = binding.toCurrencySpinner.selectedItemPosition

        binding.fromCurrencySpinner.setSelection(toPosition)
        binding.toCurrencySpinner.setSelection(fromPosition)

        // Trigger conversion after swap
        convertCurrency()
    }

    private fun showConversionLoading(loading: Boolean) {
        binding.convertProgressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.convertButton.isEnabled = !loading
        binding.convertButton.text = if (loading) "Converting..." else "Convert"
    }

    private fun navigateToHistory() {
        findNavController().navigate(R.id.action_converterFragment_to_historyFragment)
    }

    // Add this navigation method
    private fun navigateToProfile() {
        findNavController().navigate(R.id.action_converterFragment_to_profileFragment)
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_converterFragment_to_loginFragment)
    }

    private fun logout() {
        prefManager.clearUser()
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
        navigateToLogin()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}