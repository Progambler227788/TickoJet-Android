// CategoryStyleUtils.kt
package com.talhaatif.tickojet.utils

import android.content.Context
import android.content.res.ColorStateList
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.talhaatif.tickojet.R

object CategoryStyleUtils {

    fun applyCategoryStyle(view: Any, category: String) {
        val context = when (view) {
            is Chip -> view.context
            is MaterialButton -> view.context
            else -> throw IllegalArgumentException("Unsupported view type for category styling")
        }

        val categoryLower = category.lowercase()
        val (bgColor, textColor, strokeColor) = getStyleForCategory(categoryLower)

        when (view) {
            is Chip -> applyChipStyle(view, context, bgColor, textColor, strokeColor)
            is MaterialButton -> applyButtonStyle(view, context, bgColor, textColor, strokeColor)
        }
    }

    private fun applyChipStyle(
        chip: Chip,
        context: Context,
        @ColorRes bgColor: Int,
        @ColorRes textColor: Int,
        @ColorRes strokeColor: Int
    ) {
        with(chip) {
            setChipBackgroundColorResource(bgColor)
            setTextColor(ContextCompat.getColor(context, textColor))
            setChipStrokeColorResource(strokeColor)
            chipStrokeWidth = context.resources.getDimensionPixelSize(R.dimen.stroke_width).toFloat()
        }
    }

    private fun applyButtonStyle(
        button: MaterialButton,
        context: Context,
        @ColorRes bgColor: Int,
        @ColorRes textColor: Int,
        @ColorRes strokeColor: Int
    ) {
        with(button) {
            backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, bgColor))
            setTextColor(ContextCompat.getColor(context, textColor))
            setStrokeColorResource(strokeColor)
            strokeWidth = context.resources.getDimensionPixelSize(R.dimen.stroke_width)
        }
    }

    private fun getStyleForCategory(category: String): Triple<Int, Int, Int> {
        return when (category) {
            "football", "sports" -> Triple(R.color.football_color, R.color.white, R.color.football_stroke)
            "cricket" -> Triple(R.color.cricket_color, R.color.white, R.color.cricket_stroke)
            "badminton" -> Triple(R.color.badminton_color, R.color.white, R.color.badminton_stroke)
            "tennis" -> Triple(R.color.tennis_color, R.color.white, R.color.tennis_stroke)
            "basketball" -> Triple(R.color.basketball_color, R.color.white, R.color.basketball_stroke)
            "volleyball" -> Triple(R.color.volleyball_color, R.color.white, R.color.volleyball_stroke)
            "hockey" -> Triple(R.color.hockey_color, R.color.white, R.color.hockey_stroke)

            "movie", "cinema" -> Triple(R.color.movie_color, R.color.black, R.color.movie_stroke)
            "concert", "performance" -> Triple(R.color.concert_color, R.color.white, R.color.concert_stroke)
            "conference", "networking" -> Triple(R.color.conference_color, R.color.white, R.color.conference_stroke)
            "workshop" -> Triple(R.color.workshop_color, R.color.white, R.color.workshop_stroke)
            "exhibition" -> Triple(R.color.exhibition_color, R.color.white, R.color.exhibition_stroke)
            "festival" -> Triple(R.color.festival_color, R.color.white, R.color.festival_stroke)
            "charity" -> Triple(R.color.charity_color, R.color.white, R.color.charity_stroke)
            "gaming" -> Triple(R.color.gaming_color, R.color.white, R.color.gaming_stroke)

            else -> Triple(R.color.default_chip_color, R.color.black, R.color.default_chip_stroke)
        }
    }
}