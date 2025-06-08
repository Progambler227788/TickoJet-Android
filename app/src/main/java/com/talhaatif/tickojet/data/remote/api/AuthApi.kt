package com.talhaatif.tickojet.data.remote.api

import com.talhaatif.tickojet.requestModel.LoginRequest
import com.talhaatif.tickojet.requestModel.SignupRequest
import com.talhaatif.tickojet.responseModel.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {

    @POST("/api/auth/signup")
    suspend fun signup(@Body request: SignupRequest): Response<Void>

    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/api/user/fcm/register")
    suspend fun registerFcmToken(
        @Query("userId") userId: String,
        @Query("token") token: String
    ): Response<String>


}