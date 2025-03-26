package com.talhaatif.tickojet.repository

import com.talhaatif.tickojet.data.remote.client.ApiClient
import com.talhaatif.tickojet.local.TokenManager
import com.talhaatif.tickojet.responseModel.Booking
import com.talhaatif.tickojet.responseModel.SimplifiedTrendingEvent
import com.talhaatif.tickojet.utils.Result

class BookingRepository(private val tokenManager: TokenManager)  {

    suspend fun getUserBookings(): Result<List<Booking>> {
        return try {
            val token = tokenManager.getTokenForRequest()
            if (token == null) {
                return Result.Error("Not authenticated")
            }

            val response = ApiClient.bookingInstance.getBookings("Bearer $token")
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