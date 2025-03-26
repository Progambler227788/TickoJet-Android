package com.talhaatif.tickojet.utils

// this class used to handle api responses
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

/*
*
Success<T>: Holds successful API response (generic type T).

Error: Holds error message (useful for API failures, no internet, etc.).

Loading: Represents loading state (used to show a progress bar while fetching data).*/