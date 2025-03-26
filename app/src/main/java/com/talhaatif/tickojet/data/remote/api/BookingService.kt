package com.talhaatif.tickojet.data.remote.api

import com.talhaatif.tickojet.responseModel.Booking
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface BookingService {

    @GET("/api/user/profile/getBookings")
    suspend fun getBookings(
        @Header("Authorization") token: String
    ): Response<List<Booking>>


}