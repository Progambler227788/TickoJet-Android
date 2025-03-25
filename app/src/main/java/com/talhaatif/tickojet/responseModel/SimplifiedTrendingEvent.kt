package com.talhaatif.tickojet.responseModel

// SimplifiedTrendingEvent.kt
data class SimplifiedTrendingEvent(
    val id: String,
    val title: String,
    val imageUrl: String?,
    val location: String,
    val dateTime: String,
    val totalSeats: Int,
    val totalBookedSeats: Int
)