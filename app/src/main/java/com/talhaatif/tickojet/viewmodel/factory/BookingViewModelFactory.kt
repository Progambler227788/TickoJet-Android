package com.talhaatif.tickojet.viewmodel.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.talhaatif.tickojet.local.TokenManager
import com.talhaatif.tickojet.repository.AuthRepository
import com.talhaatif.tickojet.repository.BookingRepository
import com.talhaatif.tickojet.viewmodel.AuthViewModel
import com.talhaatif.tickojet.viewmodel.BookingViewModel

// to create view model objects that takes parameter so we use factory
class BookingViewModelFactory(
    private val bookingRepository: BookingRepository,
    private val tokenManager: TokenManager,
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookingViewModel::class.java)) {
            return BookingViewModel(bookingRepository, tokenManager, context) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}