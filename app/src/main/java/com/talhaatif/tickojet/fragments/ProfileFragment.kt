package com.talhaatif.tickojet.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.facebook.shimmer.Shimmer
import com.google.android.material.snackbar.Snackbar
import com.talhaatif.tickojet.LoginActivity
import com.talhaatif.tickojet.bottomsheets.CurrencyBottomSheet
import com.talhaatif.tickojet.databinding.FragmentProfileBinding
import com.talhaatif.tickojet.local.TokenManager
import com.talhaatif.tickojet.repository.ProfileRepository
import com.talhaatif.tickojet.responseModel.UserInformation
import com.talhaatif.tickojet.utils.Result
import com.talhaatif.tickojet.viewmodel.ProfileViewModel
import com.talhaatif.tickojet.viewmodel.factory.ProfileViewModelFactory


class ProfileFragment : Fragment(),CurrencyBottomSheet.CurrencySelectionListener  {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(
            ProfileRepository(TokenManager(requireContext())),
            TokenManager(requireContext()),
            requireContext()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupShimmer()
        setupClickListeners()
        observeUserData()
        loadData()
    }

    private fun setupShimmer() {
        binding.shimmerLayout.setShimmer(Shimmer.AlphaHighlightBuilder()
            .setBaseAlpha(0.8f)
            .setHighlightAlpha(0.9f)
            .setDuration(1200)
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .setAutoStart(true)
            .build())
    }

    private fun setupClickListeners() {
        binding.apply {
            editProfileBtn.setOnClickListener {
                // Handle edit profile
            }

            cityContainer.setOnClickListener {
                // Handle city selection
            }

            currencyContainer.setOnClickListener {
                showCurrencyBottomSheet()
            }

            logoutBtn.setOnClickListener {
                viewModel.logout()
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            }
        }
    }

    private fun loadData() {
        binding.apply {
            shimmerLayout.visibility = View.VISIBLE
            contentLayout.visibility = View.INVISIBLE
            errorView.visibility = View.GONE
        }
        viewModel.getUserDetails()
    }

    private fun observeUserData() {
        viewModel.userDetails.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> showLoadingState()
                is Result.Success -> {
                    hideLoadingState()
                    bindUserData(result.data)
                }
                is Result.Error -> {
                    hideLoadingState()
                    showError(result.message)
                }
            }
        }
    }

    private fun showLoadingState() {
        binding.apply {
            shimmerLayout.visibility = View.VISIBLE
            contentLayout.visibility = View.INVISIBLE
            errorView.visibility = View.GONE
        }
    }

    private fun hideLoadingState() {
        binding.apply {
            shimmerLayout.visibility = View.GONE
            contentLayout.visibility = View.VISIBLE
        }
    }

    private fun showError(message: String) {
        binding.errorView.visibility = View.VISIBLE
        binding.errorView.text = message
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
    private fun bindUserData(userInfo: UserInformation) {
        binding.apply {
            profileName.text = userInfo.userName ?: "N/A"
            profilePhone.text = userInfo.phoneNumber ?: "N/A"
            currentCity.text = userInfo.location ?: "N/A"
            currency.text = userInfo.wallet?.currencyType ?: "PKR"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showCurrencyBottomSheet() {
        val bottomSheet = CurrencyBottomSheet().apply {
            setSelectionListener(this@ProfileFragment)
        }
        bottomSheet.show(parentFragmentManager, "CurrencyBottomSheet")
    }

    override fun onCurrencySelected(currency: String) {
        viewModel.updateCurrency(currency)
        observeCurrencyUpdate()
    }

    private fun observeCurrencyUpdate() {
        viewModel.currencyUpdateResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    // Show loading for currency update if needed
                }
                is Result.Success -> {
                    // Refresh user data
                    viewModel.getUserDetails()
                    Snackbar.make(binding.root, "Currency updated successfully", Snackbar.LENGTH_SHORT).show()
                }
                is Result.Error -> {
                    Snackbar.make(binding.root, result.message, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
}