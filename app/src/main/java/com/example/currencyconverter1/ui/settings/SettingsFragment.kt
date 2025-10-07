// Create SettingsFragment.kt
package com.example.currencyconverter1.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.currencyconverter1.R
import com.example.currencyconverter1.data.CurrencyDatabase
import com.example.currencyconverter1.databinding.FragmentSettingsBinding
import com.example.currencyconverter1.utils.PrefManager
import com.example.currencyconverter1.viewmodel.UserViewModel
import com.example.currencyconverter1.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val prefManager by lazy { PrefManager.getInstance(requireContext()) }
    private val viewModel: UserViewModel by viewModels {
        ViewModelFactory(CurrencyDatabase.getInstance(requireContext()), prefManager)
    }

    private val currencies = listOf("USD", "EUR", "GBP", "JPY", "CAD", "AUD", "ZAR")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
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
        loadCurrentSettings()
    }

    private fun setupUI() {
        // Setup currency spinner
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.defaultCurrencySpinner.adapter = adapter
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.settingsState.collect { state ->
                when (state) {
                    is UserViewModel.SettingsState.Loading -> {
                        showLoading(true)
                    }
                    is UserViewModel.SettingsState.Success -> {
                        showLoading(false)
                        Toast.makeText(requireContext(), "Settings saved successfully!", Toast.LENGTH_SHORT).show()
                        applyThemeSettings()
                    }
                    is UserViewModel.SettingsState.Error -> {
                        showLoading(false)
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        showLoading(false)
                    }
                }
            }
        }
    }

    private fun loadCurrentSettings() {
        val user = prefManager.getCurrentUser()

        binding.notificationsSwitch.isChecked = user.notificationsEnabled
        binding.darkModeSwitch.isChecked = user.darkMode

        val currencyPosition = currencies.indexOf(user.defaultCurrency)
        if (currencyPosition != -1) {
            binding.defaultCurrencySpinner.setSelection(currencyPosition)
        }

        applyThemeSettings()
    }

    private fun setupClickListeners() {
        binding.saveButton.setOnClickListener {
            saveSettings()
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.clearHistoryButton.setOnClickListener {
            clearHistory()
        }
    }

    private fun saveSettings() {
        val notificationsEnabled = binding.notificationsSwitch.isChecked
        val darkMode = binding.darkModeSwitch.isChecked
        val defaultCurrency = binding.defaultCurrencySpinner.selectedItem.toString()

        viewModel.updateSettings(notificationsEnabled, darkMode, defaultCurrency)
    }

    private fun clearHistory() {
        // You'll need to implement this in ConversionViewModel
        Toast.makeText(requireContext(), "Clear history functionality to be implemented", Toast.LENGTH_SHORT).show()
    }

    private fun applyThemeSettings() {
        val darkMode = prefManager.getDarkMode()
        if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun showLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.saveButton.isEnabled = !loading
        binding.saveButton.text = if (loading) "Saving..." else "Save Settings"
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_settingsFragment_to_loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}