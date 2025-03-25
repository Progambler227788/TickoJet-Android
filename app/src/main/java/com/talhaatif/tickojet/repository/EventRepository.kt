package com.talhaatif.tickojet.repository

import com.talhaatif.tickojet.data.remote.client.ApiClient
import com.talhaatif.tickojet.local.TokenManager
import com.talhaatif.tickojet.responseModel.Event
import com.talhaatif.tickojet.responseModel.SimplifiedTrendingEvent
import com.talhaatif.tickojet.utils.Result

// EventRepository.kt
class EventRepository(private val tokenManager: TokenManager) {
    suspend fun getTrendingEvents(): Result<List<SimplifiedTrendingEvent>> {
        return try {
            val token = tokenManager.getTokenForRequest()
            if (token == null) {
                return Result.Error("Not authenticated")
            }

            val response = ApiClient.instance.getTrendingEvents("Bearer $token")
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error(response.message())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun getEventById(eventId: String): Result<Event> {
        return try {
            val token = tokenManager.getTokenForRequest()
            if (token == null) {
                return Result.Error("Not authenticated")
            }

            val response = ApiClient.instance.getEventByID("Bearer $token", eventId)

            if (response.isSuccessful) {
                response.body()?.let { event ->
                    Result.Success(event)
                } ?: Result.Error("Event data was null")
            } else {
                val errorMessage = when (response.code()) {
                    401 -> "Unauthorized - Please login again"
                    404 -> "Event not found"
                    500 -> "Server error"
                    else -> response.message().takeIf { it.isNotBlank() }
                        ?: "Failed to fetch event (HTTP ${response.code()})"
                }
                Result.Error(errorMessage)
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message ?: "Unknown network error"}")
        }
    }

}