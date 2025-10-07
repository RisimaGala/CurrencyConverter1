package com.example.currencyconverter1.ui.converter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyconverter1.R
import com.example.currencyconverter1.data.CurrencyDatabase
import com.example.currencyconverter1.databinding.FragmentHistoryBinding
import com.example.currencyconverter1.utils.PrefManager
import com.example.currencyconverter1.viewmodel.ConversionViewModel
import com.example.currencyconverter1.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val prefManager by lazy { PrefManager.getInstance(requireContext()) }
    private val viewModel: ConversionViewModel by viewModels {
        ViewModelFactory(CurrencyDatabase.getInstance(requireContext()), prefManager)
    }
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!prefManager.isUserLoggedIn()) {
            navigateToLogin()
            return
        }

        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        viewModel.loadConversionHistory()
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter()
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.historyRecyclerView.adapter = adapter
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.historyState.collect { state ->
                when (state) {
                    is ConversionViewModel.HistoryState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is ConversionViewModel.HistoryState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        adapter.submitList(state.history)
                        binding.emptyText.visibility = if (state.history.isEmpty()) View.VISIBLE else View.GONE
                    }
                    is ConversionViewModel.HistoryState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
            Toast.makeText(requireContext(), "History cleared", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}