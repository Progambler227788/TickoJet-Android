package com.talhaatif.tickojet.utils

import java.text.SimpleDateFormat
import java.util.Locale

// DateUtils.kt
object DateUtils {
    private val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
    private val outputFormat = SimpleDateFormat("EEE, MMM d • HH:mm", Locale.getDefault())

    fun formatEventDate(isoDateString: String): String {
        return try {
            val date = inputFormat.parse(isoDateString)
            outputFormat.format(date)
        } catch (e: Exception) {
            "Date unavailable" // Fallback text
        }
    }
}