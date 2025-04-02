package com.talhaatif.tickojet.data.remote.api

import com.talhaatif.tickojet.responseModel.Event
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SearchService {

    @GET("/api/user/profile/events/searchByCategory") // endpoint
    suspend fun searchByCategory(
        @Header("Authorization") token: String,
        @Query("category") category: String  // Pass category in query ?category=Sports
    ): Response<List<Event>> // Returns full Event object with seats
}