package com.talhaatif.tickojet.responseModel

import com.google.gson.annotations.SerializedName


enum class BookingStatus {
    @SerializedName("PENDING") PENDING,
    @SerializedName("CONFIRMED") CONFIRMED,
    @SerializedName("CANCELLED") CANCELLED
}
