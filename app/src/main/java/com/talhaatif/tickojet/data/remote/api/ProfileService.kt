package com.talhaatif.tickojet.data.remote.api


import com.talhaatif.tickojet.responseModel.UserInformation
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
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




}