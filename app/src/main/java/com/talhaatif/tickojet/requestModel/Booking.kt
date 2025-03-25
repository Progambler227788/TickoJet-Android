package com.talhaatif.tickojet.requestModel

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.talhaatif.tickojet.adapter.EpochMillisDateAdapter
import java.util.Date

data class Booking(
    val id: String,
    val userId: String,
    val eventId: String,
    val seats: List<Seat>,
    val status: BookingStatus,
    @SerializedName("createdAt")
    @JsonAdapter(EpochMillisDateAdapter::class)
    val createdAt: Date
)