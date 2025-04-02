package com.talhaatif.tickojet.repository

import com.talhaatif.tickojet.data.remote.client.ApiClient
import com.talhaatif.tickojet.local.TokenManager
import com.talhaatif.tickojet.responseModel.Event
import com.talhaatif.tickojet.responseModel.SimplifiedTrendingEvent
import com.talhaatif.tickojet.utils.Result

class SearchRepository(private val tokenManager: TokenManager) {

    suspend fun getEventsByCategory(category: String): Result<List<Event>> {
        return try {
            val token = tokenManager.getTokenForRequest()
            if (token == null) {
                return Result.Error("Not authenticated")
            }

            val response = ApiClient.searchInstance.searchByCategory("Bearer $token",category)
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error(response.message())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unknown error occurred")
        }
    }

}