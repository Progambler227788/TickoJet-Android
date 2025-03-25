package com.talhaatif.tickojet.data.remote.api

import com.talhaatif.tickojet.requestModel.Booking
import com.talhaatif.tickojet.requestModel.SimplifiedTrendingEvent
import retrofit2.Response

import retrofit2.http.GET
import retrofit2.http.Header


interface EventService {
    @GET("/api/user/profile/events/trendingEvents")
    suspend fun getTrendingEvents(
        @Header("Authorization") token: String
    ): Response<List<SimplifiedTrendingEvent>>


    @GET("/api/user/profile/getBookings")
    suspend fun getBookings(): Response<List<Booking>>

    // Add other endpoints as needed
}