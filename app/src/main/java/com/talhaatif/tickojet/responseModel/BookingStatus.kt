package com.talhaatif.tickojet.responseModel

import com.google.gson.annotations.SerializedName

// SerializedName makes sure that if our variable name is different then it will work
// with value given in serialized that is actual key in Database or in our external api backend

enum class BookingStatus {
    @SerializedName("PENDING") PENDING,
    @SerializedName("CONFIRMED") CONFIRMED,
    @SerializedName("CANCELLED") CANCELLED
}
