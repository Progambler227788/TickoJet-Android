package com.talhaatif.tickojet.responseModel

data class Event(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val category: String,
    val location: String,
    val dateTime: String, // Format it properly if needed (e.g., `yyyy-MM-dd'T'HH:mm:ss'Z'`)
    val seats: List<Seat>,
    val basePrice: Double,
    val rating: Double,
    val totalBookedSeats: Int,
    val totalSeats: Int,
    val termsAndConditions: String
)
