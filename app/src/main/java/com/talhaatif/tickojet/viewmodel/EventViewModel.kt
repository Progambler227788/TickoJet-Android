package com.talhaatif.tickojet.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.talhaatif.tickojet.local.TokenManager
import com.talhaatif.tickojet.repository.EventRepository
import com.talhaatif.tickojet.responseModel.BookingResponse
import com.talhaatif.tickojet.responseModel.Event
import com.talhaatif.tickojet.responseModel.SimplifiedTrendingEvent
import com.talhaatif.tickojet.utils.NetworkUtils
import com.talhaatif.tickojet.utils.Result
import kotlinx.coroutines.launch

// EventViewModel.kt
class EventViewModel(
    private val eventRepository: EventRepository,
    private val tokenManager: TokenManager,
    private val context: Context
) : ViewModel() {

    private val _trendingEvents = MutableLiveData<Result<List<SimplifiedTrendingEvent>>>()
    val trendingEvents: LiveData<Result<List<SimplifiedTrendingEvent>>> get() = _trendingEvents


    private val _eventDetails = MutableLiveData<Result<Event>>()
    val eventDetails: LiveData<Result<Event>> get() = _eventDetails


    // New LiveData for booking state
    private val _bookingState = MutableLiveData<Result<BookingResponse>>()
    val bookingState: LiveData<Result<BookingResponse>> get() = _bookingState


    fun getTrendingEvents() {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            _trendingEvents.value = Result.Error("No internet connection")
            return
        }

        _trendingEvents.value = Result.Loading
        viewModelScope.launch {
            _trendingEvents.value = eventRepository.getTrendingEvents()
        }
    }

    fun getEventById(eventId: String) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            _eventDetails.value = Result.Error("No internet connection. Please check your network and try again.")
            return
        }

        _eventDetails.value = Result.Loading
        viewModelScope.launch {
            try {
                val result = eventRepository.getEventById(eventId)
                _eventDetails.value = when (result) {
                    is Result.Success -> result
                    is Result.Error -> Result.Error(result.message ?: "Unknown error occurred")
                    Result.Loading -> Result.Error("Unexpected loading state")
                }
            } catch (e: Exception) {
                _eventDetails.value = Result.Error("Failed to fetch event: ${e.message ?: "Unknown error"}")
            }
        }
    }


    // New function for wallet booking
    fun bookWithWallet(eventId: String, seatNumbers: List<String>) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            _bookingState.value = Result.Error("No internet connection")
            return
        }

        _bookingState.value = Result.Loading
        viewModelScope.launch {
            try {
                val result = eventRepository.bookWithWallet(eventId, seatNumbers)
                _bookingState.value = when (result) {
                    is Result.Success -> result
                    is Result.Error -> Result.Error(result.message)
                    Result.Loading -> Result.Error("Unexpected loading state")
                }
            } catch (e: Exception) {
                _bookingState.value = Result.Error("Booking failed: ${e.message ?: "Unknown error"}")
            }
        }
    }
}