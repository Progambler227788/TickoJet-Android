package com.talhaatif.tickojet.repository

import com.talhaatif.tickojet.data.remote.client.ApiClient
import com.talhaatif.tickojet.local.TokenManager
import com.talhaatif.tickojet.requestModel.UpdateRequest
import com.talhaatif.tickojet.responseModel.UserInformation
import com.talhaatif.tickojet.utils.Result

class ProfileRepository(private val tokenManager: TokenManager) {

    suspend fun getUserInformation(): Result<UserInformation> {
        return try {
            val token = tokenManager.getTokenForRequest() ?: return Result.Error("Not authenticated")

            val response = ApiClient.profileInstance.getUserInformation("Bearer $token")

            if (response.isSuccessful) {
                response.body()?.let { user ->
                    Result.Success(user)
                } ?: Result.Error("User data was null")
            } else {
                Result.Error(
                    when (response.code()) {
                        401 -> "Unauthorized - Please login again"
                        404 -> "User not found"
                        500 -> "Server error"
                        else -> response.message().takeIf { it.isNotBlank() }
                            ?: "Failed to fetch user details (HTTP ${response.code()})"
                    }
                )
            }
        } catch (e: Exception) {
            Result.Error(e.message.toString())
        }
    }

    suspend fun updateCurrency(currencyType: String): Result<Map<String, String>> {
        return try {
            val token = tokenManager.getTokenForRequest() ?: return Result.Error("Not authenticated")

            val response = ApiClient.profileInstance.updateCurrency(
                "Bearer $token",
                currencyType
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Empty response body")
            } else {
                Result.Error(
                    when (response.code()) {
                        400 -> "Invalid currency type"
                        401 -> "Unauthorized - Please login again"
                        else -> "Failed to update currency (HTTP ${response.code()})"
                    }
                )
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    suspend fun updateLocation(location: String): Result<Map<String, String>> {
        return try {
            val token = tokenManager.getTokenForRequest() ?: return Result.Error("Not authenticated")

            val response = ApiClient.profileInstance.updateLocation(
                "Bearer $token",
                location
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Empty response body")
            } else {
                Result.Error(
                    when (response.code()) {
                        400 -> "Invalid location"
                        401 -> "Unauthorized - Please login again"
                        else -> "Failed to update location (HTTP ${response.code()})"
                    }
                )
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }


    suspend fun updateProfile(updateRequest: UpdateRequest): Result<Map<String, String>> {
        return try {
            val token = tokenManager.getTokenForRequest() ?: return Result.Error("Not authenticated")

            val response = ApiClient.profileInstance.updateProfile(
                "Bearer $token",
                updateRequest
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Empty response body")
            } else {
                Result.Error(
                    when (response.code()) {
                        400 -> "Invalid data"
                        401 -> "Unauthorized - Please login again"
                        else -> "Failed to update profile (HTTP ${response.code()})"
                    }
                )
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }
}