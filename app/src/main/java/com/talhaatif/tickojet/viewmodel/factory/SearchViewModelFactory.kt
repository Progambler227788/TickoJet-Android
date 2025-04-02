package com.talhaatif.tickojet.viewmodel.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.talhaatif.tickojet.local.TokenManager
import com.talhaatif.tickojet.repository.SearchRepository
import com.talhaatif.tickojet.viewmodel.SearchViewModel

class SearchViewModelFactory(
    private val searchRepository: SearchRepository,
    private val tokenManager: TokenManager,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(searchRepository, tokenManager, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}