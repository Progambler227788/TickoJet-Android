package com.talhaatif.tickojet.responseModel

data class Payment (
    val amount: Double = 0.0,
    val method: String? = null,// ENUM: CARD, PAYPAL, WALLET
    val status: PaymentStatus? = null,// ENUM: SUCCESS, FAILED, PENDING
    val timestamp: String? = null,
    val userId: String? = null,
)