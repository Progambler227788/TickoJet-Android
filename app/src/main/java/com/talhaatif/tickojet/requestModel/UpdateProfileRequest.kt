package com.talhaatif.tickojet.requestModel

data class UpdateProfileRequest(
    val userName: String,
    val email: String,
    val phoneNumber: String,
    val location:String
)