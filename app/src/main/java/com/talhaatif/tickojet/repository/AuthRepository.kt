package com.talhaatif.tickojet.repository

import com.talhaatif.tickojet.data.remote.client.AuthClient
import com.talhaatif.tickojet.requestModel.LoginRequest
import com.talhaatif.tickojet.requestModel.SignupRequest
import com.talhaatif.tickojet.requestModel.UpdateProfileRequest
import com.talhaatif.tickojet.responseModel.LoginResponse
import retrofit2.Response

class AuthRepository {

    suspend fun signup(userName: String, email: String, password: String): Response<Void> {
        val request = SignupRequest(userName, email, password)
        return AuthClient.authApi.signup(request)
    }

    suspend fun login(userName: String, password: String): Response<LoginResponse> {
        val request = LoginRequest(userName, password)
        return AuthClient.authApi.login(request)
    }

}