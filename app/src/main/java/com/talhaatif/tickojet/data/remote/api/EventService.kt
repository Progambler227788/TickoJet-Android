package com.talhaatif.tickojet.data.remote.api

import com.talhaatif.tickojet.responseModel.Booking
import com.talhaatif.tickojet.responseModel.Event
import com.talhaatif.tickojet.responseModel.SimplifiedTrendingEvent
import retrofit2.Response

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path


interface EventService {
    @GET("/api/user/profile/events/trendingEvents")
    suspend fun getTrendingEvents(
        @Header("Authorization") token: String
    ): Response<List<SimplifiedTrendingEvent>>

    @GET("/api/user/profile/events/{eventId}") // endpoint
    suspend fun getEventByID(
        @Header("Authorization") token: String,
        @Path("eventId") eventId: String  // Pass eventId in URL
    ): Response<Event> // Returns full Event object with seats

    @GET("/api/user/profile/getBookings")
    suspend fun getBookings(
        @Header("Authorization") token: String
    ): Response<List<Booking>>

}