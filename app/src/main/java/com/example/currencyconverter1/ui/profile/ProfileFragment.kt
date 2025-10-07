// Create ProfileFragment.kt
package com.example.currencyconverter1.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.currencyconverter1.R
import com.example.currencyconverter1.data.CurrencyDatabase
import com.example.currencyconverter1.databinding.FragmentProfileBinding
import com.example.currencyconverter1.utils.PrefManager
import com.example.currencyconverter1.viewmodel.UserViewModel
import com.example.currencyconverter1.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val prefManager by lazy { PrefManager.getInstance(requireContext()) }
    private val viewModel: UserViewModel by viewModels {
        ViewModelFactory(CurrencyDatabase.getInstance(requireContext()), prefManager)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!prefManager.isUserLoggedIn()) {
            navigateToLogin()
            return
        }

        setupObservers()
        setupClickListeners()
        loadUserData()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.profileState.collect { state ->
                when (state) {
                    is UserViewModel.ProfileState.Loading -> {
                        showLoading(true)
                    }
                    is UserViewModel.ProfileState.Success -> {
                        showLoading(false)
                        displayUserData(state.user)
                        Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    }
                    is UserViewModel.ProfileState.Error -> {
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

    private fun loadUserData() {
        val user = prefManager.getCurrentUser()
        displayUserData(user)
    }

    private fun displayUserData(user: com.example.currencyconverter1.data.entity.User) {
        binding.nameEditText.setText(user.name)
        binding.emailEditText.setText(user.email)
        binding.phoneEditText.setText(user.phone)
        binding.countryEditText.setText(user.country)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        binding.memberSinceText.text = "Member since: ${dateFormat.format(Date(user.createdAt))}"

        binding.conversionsCountText.text = "Total conversions: Loading..." // You can add this tracking
    }

    private fun setupClickListeners() {
        binding.saveButton.setOnClickListener {
            updateProfile()
        }

        binding.settingsButton.setOnClickListener {
            navigateToSettings()
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun updateProfile() {
        val name = binding.nameEditText.text.toString().trim()
        val phone = binding.phoneEditText.text.toString().trim()
        val country = binding.countryEditText.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter your name", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = prefManager.getCurrentUserId()
        viewModel.updateProfile(userId, name, phone, country)
    }

    private fun showLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.saveButton.isEnabled = !loading
        binding.saveButton.text = if (loading) "Saving..." else "Save Changes"
    }

    private fun navigateToSettings() {
        findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}