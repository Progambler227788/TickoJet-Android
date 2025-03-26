package com.talhaatif.tickojet.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.talhaatif.tickojet.local.TokenManager
import com.talhaatif.tickojet.repository.BookingRepository
import com.talhaatif.tickojet.responseModel.Booking
import com.talhaatif.tickojet.utils.NetworkUtils
import com.talhaatif.tickojet.utils.Result
import kotlinx.coroutines.launch

class BookingViewModel(
    private val bookingRepository: BookingRepository,
    private val tokenManager: TokenManager,
    private val context: Context
) : ViewModel() {

    private val _bookings = MutableLiveData<Result<List<Booking>>>()
    val bookings: LiveData<Result<List<Booking>>> get() = _bookings

    fun getBookings() {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            _bookings.value = Result.Error("No internet connection")
            return
        }

        _bookings.value = Result.Loading
        viewModelScope.launch {
            try {
                val result = bookingRepository.getUserBookings()
                _bookings.value = when (result) {
                    is Result.Success -> result
                    is Result.Error -> Result.Error(result.message ?: "Unknown error")
                    Result.Loading -> Result.Error("Unexpected loading state")
                }
            } catch (e: Exception) {
                _bookings.value = Result.Error("Failed to fetch bookings: ${e.message}")
            }
        }
    }
}