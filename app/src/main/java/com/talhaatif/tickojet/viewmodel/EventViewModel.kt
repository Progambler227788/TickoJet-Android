package com.talhaatif.tickojet.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.talhaatif.tickojet.local.TokenManager
import com.talhaatif.tickojet.repository.EventRepository
import com.talhaatif.tickojet.requestModel.SimplifiedTrendingEvent
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
}