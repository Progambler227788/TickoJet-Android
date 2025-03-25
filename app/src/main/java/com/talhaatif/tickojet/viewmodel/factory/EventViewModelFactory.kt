package com.talhaatif.tickojet.viewmodel.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.talhaatif.tickojet.local.TokenManager
import com.talhaatif.tickojet.repository.EventRepository
import com.talhaatif.tickojet.viewmodel.EventViewModel

// EventViewModelFactory.kt
class EventViewModelFactory(
    private val eventRepository: EventRepository,
    private val tokenManager: TokenManager,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return EventViewModel(eventRepository, tokenManager, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}