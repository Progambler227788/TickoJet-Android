package com.talhaatif.tickojet.responseModel

data class SeatUpdate(
    val eventId: String,
    val seatNumbers: List<String>,
    val status: String // "BOOKED", "AVAILABLE", etc.
)