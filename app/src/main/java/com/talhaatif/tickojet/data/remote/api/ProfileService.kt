package com.talhaatif.tickojet.data.remote.api


import com.talhaatif.tickojet.requestModel.UpdateRequest
import com.talhaatif.tickojet.responseModel.UserInformation
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Query

interface ProfileService {

    @GET("/api/user/profile/details")
    suspend fun getUserInformation(
        @Header("Authorization") token: String
    ): Response<UserInformation>


    @PUT("/api/user/profile/wallet/updateCurrency")
    suspend fun updateCurrency(
        @Header("Authorization") token: String,
        @Query("currencyType") currencyType: String
    ) : Response<Map<String,String>>



    @PUT("/api/user/profile/updateLocation")
    suspend fun updateLocation(
        @Header("Authorization") token: String,
        @Query("location") location: String
    ) : Response<Map<String,String>>


    @PUT("/api/auth/updateProfile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body updateRequest: UpdateRequest
    ): Response<Map<String, String>>




}