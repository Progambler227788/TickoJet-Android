package com.talhaatif.tickojet.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.talhaatif.tickojet.local.TokenManager
import com.talhaatif.tickojet.repository.ProfileRepository
import com.talhaatif.tickojet.requestModel.UpdateRequest
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


    // currency
    private val _locationUpdateResult = MutableLiveData<Result<Map<String, String>>>()
    val locationUpdateResult: LiveData<Result<Map<String, String>>> get() = _locationUpdateResult

   // profile update
    private val _profileUpdateResult = MutableLiveData<Result<Map<String, String>>>()
    val profileUpdateResult: LiveData<Result<Map<String, String>>> get() = _profileUpdateResult


    // update the user currency
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

    // update the user location
    fun updateLocation(location: String) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            _locationUpdateResult.value  = Result.Error("No internet connection")
            return
        }

        _locationUpdateResult.value = Result.Loading
        viewModelScope.launch {
            _locationUpdateResult.value  = profileRepository.updateLocation(location)
        }
    }


    // fetch the user details
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

    // Logout the user
    fun logout() {
        viewModelScope.launch {
            try {
                tokenManager.clearToken()
            } catch (e: Exception) {
                _userDetails.value = Result.Error("Logout failed: ${e.message}")
            }
        }
    }



    fun updateProfile(updateRequest: UpdateRequest) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            _profileUpdateResult.value = Result.Error("No internet connection")
            return
        }

        _profileUpdateResult.value = Result.Loading
        viewModelScope.launch {
            _profileUpdateResult.value = profileRepository.updateProfile(updateRequest)
        }
    }
}