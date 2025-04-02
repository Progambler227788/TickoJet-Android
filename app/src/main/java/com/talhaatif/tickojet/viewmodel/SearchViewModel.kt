package com.talhaatif.tickojet.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.talhaatif.tickojet.local.TokenManager
import com.talhaatif.tickojet.repository.EventRepository
import com.talhaatif.tickojet.repository.SearchRepository
import com.talhaatif.tickojet.responseModel.Event
import com.talhaatif.tickojet.utils.NetworkUtils
import com.talhaatif.tickojet.utils.Result
import kotlinx.coroutines.launch

class SearchViewModel(private val searchRepository: SearchRepository,
                      private val tokenManager: TokenManager,
                      private val context: Context
) : ViewModel() {

    private val _eventsByCategory = MutableLiveData<Result<List<Event>>>()
    val eventsByCategory: LiveData<Result<List<Event>>> get() = _eventsByCategory

    fun getEventsByCategory(category: String) {

        if (!NetworkUtils.isNetworkAvailable(context)) {
            _eventsByCategory.value = Result.Error("No internet connection")
            return
        }

        _eventsByCategory.value = Result.Loading
        viewModelScope.launch {
            _eventsByCategory.value = searchRepository.getEventsByCategory(category)
        }

    }

}
