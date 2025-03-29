package com.talhaatif.tickojet.repository

import android.util.Log
import com.talhaatif.tickojet.data.remote.client.ApiClient
import com.talhaatif.tickojet.local.TokenManager
import com.talhaatif.tickojet.responseModel.BookingResponse
import com.talhaatif.tickojet.responseModel.Event
import com.talhaatif.tickojet.responseModel.SimplifiedTrendingEvent
import com.talhaatif.tickojet.responseModel.UpcomingEvents
import com.talhaatif.tickojet.utils.Result
import java.io.IOException
import java.net.URLEncoder

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

    suspend fun getUpcomingEvents(): Result<List<UpcomingEvents>> {
        return try {
            val token = tokenManager.getTokenForRequest()
            if (token == null) {
                return Result.Error("Not authenticated")
            }

            val response = ApiClient.instance.getUpcomingEvents("Bearer $token")
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

    suspend fun bookWithWallet(
        eventId: String,
        seatNumbers: List<String>
    ): Result<BookingResponse> {
        return try {
            // 1. Get authentication token
            val token = tokenManager.getTokenForRequest()
            if (token == null) {
                return Result.Error("Not authenticated")
            }
            // 2. Convert seatNumbers to a comma-separated string
            val seatNumbersString = seatNumbers.joinToString(",") { URLEncoder.encode(it, "UTF-8") }

            // 3. Make API call
            val response = ApiClient.instance.bookWithWallet(
                token = "Bearer $token",
                eventId = eventId,
                seatNumbers = seatNumbersString
            )

            // 4. Handle response
            when {
                response.isSuccessful -> {
                    response.body()?.let { bookingResponse ->
                        Result.Success(bookingResponse)
                    } ?: Result.Error("Empty response body")
                }

                response.code() == 401 -> {
                    Result.Error("Session expired, please login again")
                }

                response.code() == 400 -> {
                    val errorBody = response.errorBody()?.string()
                    Result.Error(errorBody ?: "Bad request")
                }

                else -> {
                    Result.Error("Booking failed: ${response.message()}")
                }
            }
        } catch (e: IOException) {
            Result.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unknown error occurred")
        }
    }

}