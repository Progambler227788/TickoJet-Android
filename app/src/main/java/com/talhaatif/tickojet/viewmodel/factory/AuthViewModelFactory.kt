package com.talhaatif.tickojet.viewmodel.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.talhaatif.tickojet.local.TokenManager
import com.talhaatif.tickojet.repository.AuthRepository
import com.talhaatif.tickojet.viewmodel.AuthViewModel

class AuthViewModelFactory(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager,
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authRepository, tokenManager, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}