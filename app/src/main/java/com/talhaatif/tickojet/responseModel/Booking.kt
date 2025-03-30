package com.talhaatif.tickojet.responseModel

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.util.Date

data class Booking(
    val id: String,
    val userId: String,
    val eventId: String,
    val eventName: String,
    val eventDate: String,
    val seats: List<Seat>,
    val status: BookingStatus,
    val createdAt: String,
    val payment: Payment
)