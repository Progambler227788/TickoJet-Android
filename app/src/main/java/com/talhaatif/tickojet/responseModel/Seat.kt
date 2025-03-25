package com.talhaatif.tickojet.responseModel

data class Seat(
    val seatNumber: String,
    val available: Boolean,
    val price: Double,
    val version: Long
)
