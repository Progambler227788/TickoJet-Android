package com.talhaatif.tickojet.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.talhaatif.tickojet.local.TokenManager
import com.talhaatif.tickojet.repository.ProfileRepository
import com.talhaatif.tickojet.responseModel.UserInformation
import com.talhaatif.tickojet.utils.NetworkUtils
import com.talhaatif.tickojet.utils.Result
import kotlinx.coroutines.launch



class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val tokenManager: TokenManager,
    private val context: Context
) : ViewModel() {

    // user details

    private val _userDetails = MutableLiveData<Result<UserInformation>>()
    val userDetails: LiveData<Result<UserInformation>> get() = _userDetails


    // currency
    private val _currencyUpdateResult = MutableLiveData<Result<Map<String, String>>>()
    val currencyUpdateResult: LiveData<Result<Map<String, String>>> get() = _currencyUpdateResult

    fun updateCurrency(currencyType: String) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            _currencyUpdateResult.value = Result.Error("No internet connection")
            return
        }

        _currencyUpdateResult.value = Result.Loading
        viewModelScope.launch {
            _currencyUpdateResult.value = profileRepository.updateCurrency(currencyType)
        }
    }

    fun getUserDetails() {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            _userDetails.value = Result.Error("No internet connection")
            return
        }

        _userDetails.value = Result.Loading
        viewModelScope.launch {
            _userDetails.value = when (val result = profileRepository.getUserInformation()) {
                is Result.Success -> result
                is Result.Error -> result
                Result.Loading -> Result.Error("Unexpected loading state from repository")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                tokenManager.clearToken()
            } catch (e: Exception) {
                _userDetails.value = Result.Error("Logout failed: ${e.message}")
            }
        }
    }
}