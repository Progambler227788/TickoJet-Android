package com.talhaatif.tickojet.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.talhaatif.tickojet.local.TokenManager
import com.talhaatif.tickojet.repository.AuthRepository
import com.talhaatif.tickojet.utils.NetworkUtils
import com.talhaatif.tickojet.utils.Result
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager,
    private val context: Context // Pass context for network checks
) : ViewModel() {

    // LiveData for login state
    private val _loginState = MutableLiveData<Result<String>>()
    val loginState: LiveData<Result<String>> get() = _loginState

    // LiveData for signup state
    private val _signupState = MutableLiveData<Result<Boolean>>()
    val signupState: LiveData<Result<Boolean>> get() = _signupState

    // LiveData for error messages
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun login(userName: String, password: String) {
        // Input validation
        if (userName.isEmpty() || password.isEmpty()) {
            _loginState.value = Result.Error("Please fill in all fields")
            return
        }

        // Network availability check
        if (!NetworkUtils.isNetworkAvailable(context)) {
            _loginState.value = Result.Error("No internet connection. Please turn on the internet.")
            return
        }

        // Start loading
        _loginState.value = Result.Loading

        viewModelScope.launch {
            try {
                val response = authRepository.login(userName, password)
                if (response.isSuccessful) {
                    val token = response.body()?.token
                    if (token != null) {
                        tokenManager.saveToken(token) // Save token to DataStore
                        _loginState.value = Result.Success(token)
                    } else {
                        _loginState.value = Result.Error("Invalid credentials")
                    }
                } else {
                    _loginState.value = Result.Error("Login failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _loginState.value = Result.Error("Network error: ${e.message}")
            }
        }
    }

    fun signup(userName: String, email: String, password: String, confirmPassword: String) {
        // Input validation
        if (userName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            _signupState.value = Result.Error("Please fill in all fields")
            return
        }

        // Password match validation
        if (password != confirmPassword) {
            _signupState.value = Result.Error("Passwords do not match")
            return
        }

        // Network availability check
        if (!NetworkUtils.isNetworkAvailable(context)) {
            _signupState.value = Result.Error("No internet connection. Please turn on the internet.")
            return
        }

        // Start loading
        _signupState.value = Result.Loading

        viewModelScope.launch {
            try {
                val response = authRepository.signup(userName, email, password)
                if (response.isSuccessful) {
                    _signupState.value = Result.Success(true)
                } else {
                    _signupState.value = Result.Error("Signup failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _signupState.value = Result.Error("Network error: ${e.message}")
            }
        }
    }
}