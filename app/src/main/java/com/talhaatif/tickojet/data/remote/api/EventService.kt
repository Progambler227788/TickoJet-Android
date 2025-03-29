package com.talhaatif.tickojet.data.remote.api

import com.talhaatif.tickojet.responseModel.Booking
import com.talhaatif.tickojet.responseModel.BookingResponse
import com.talhaatif.tickojet.responseModel.Event
import com.talhaatif.tickojet.responseModel.SimplifiedTrendingEvent
import com.talhaatif.tickojet.responseModel.UpcomingEvents
import retrofit2.Response

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface EventService {
    @GET("/api/user/profile/events/trendingEvents")
    suspend fun getTrendingEvents(
        @Header("Authorization") token: String
    ): Response<List<SimplifiedTrendingEvent>>

    @GET("/api/user/profile/events/upcomingEvents")
    suspend fun getUpcomingEvents(
        @Header("Authorization") token: String
    ): Response<List<UpcomingEvents>>

    @GET("/api/user/profile/events/{eventId}") // endpoint
    suspend fun getEventByID(
        @Header("Authorization") token: String,
        @Path("eventId") eventId: String  // Pass eventId in URL
    ): Response<Event> // Returns full Event object with seats

    @POST("/api/user/profile/book-with-wallet")
    suspend fun bookWithWallet(
        @Header("Authorization") token: String,
        @Query("eventId") eventId: String,
        @Query("seatNumbers",encoded = true) seatNumbers: String // Accept List instead of String
    ): Response<BookingResponse>


}