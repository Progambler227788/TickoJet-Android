package com.talhaatif.tickojet.responseModel

data class StripeIntentResponse(
    val clientSecret: String,
    val ephemeralKey: String,
    val customerId: String,
    val publishableKey: String
)