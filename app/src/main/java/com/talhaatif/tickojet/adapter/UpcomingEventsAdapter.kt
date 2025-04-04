package com.talhaatif.tickojet.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.talhaatif.tickojet.BookEventActivity
import com.talhaatif.tickojet.R
import com.talhaatif.tickojet.databinding.RvEventBinding
import com.talhaatif.tickojet.responseModel.UpcomingEvents
import com.talhaatif.tickojet.utils.CategoryStyleUtils
import com.talhaatif.tickojet.utils.DateUtils

class UpcomingEventsAdapter
: ListAdapter<UpcomingEvents, UpcomingEventsAdapter.UpcomingEventViewHolder>(EventDiffCallback()) {

    inner class UpcomingEventViewHolder(
        private val binding: RvEventBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: UpcomingEvents) {
            with(binding) {

                binding.root.setOnClickListener {
                    val context = it.context
                    val intent = Intent(context, BookEventActivity::class.java)

                    intent.putExtra("EVENT_ID", event.id)
                    context.startActivity(intent)
                }

                // Set text values
                tvTitle.text = event.title ?: "Event"
                tvLocation.text = event.location ?: "Location not specified"

                // Format date
                tvDateTime.text = event.dateTime?.let { DateUtils.formatEventDate(it) } ?: "Date not specified"

                val drawables = listOf(R.drawable.a1,R.drawable.a4, R.drawable.a5, R.drawable.event1)

                // Load image with Glide
                event.imageUrl?.let { url ->
                    Glide.with(root.context)
                        .load(drawables.random())
                        .placeholder(R.drawable.a1)
                        .error(R.drawable.a1)
                        .into(ivEvent)
                } ?: ivEvent.setImageResource(R.drawable.a1)

                // Handling category chip


                event.category?.let { category ->
                    chipCategory.text = category


                    // applying style
                    CategoryStyleUtils.applyCategoryStyle(binding.chipCategory, category)


                    chipCategory.visibility = View.VISIBLE
                } ?: run {
                    chipCategory.visibility = View.GONE
                }



            }
        }
        private fun applyChipStyle(category: String) {
            val context = binding.root.context
            val categoryLower = category.lowercase()

            val (bgColor, textColor, strokeColor) = when (categoryLower) {
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
   // apply colors on stroke, text, background
            with(binding.chipCategory) {
                setChipBackgroundColorResource(bgColor)

                setTextColor(context.getColor(textColor))
                setChipStrokeColorResource(strokeColor)
                chipStrokeWidth = 1.5f
            }
        }


    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingEventViewHolder {
        val binding = RvEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UpcomingEventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UpcomingEventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private class EventDiffCallback : DiffUtil.ItemCallback<UpcomingEvents>() {
    override fun areItemsTheSame(oldItem: UpcomingEvents, newItem: UpcomingEvents): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UpcomingEvents, newItem: UpcomingEvents): Boolean {
        return oldItem.title == newItem.title &&
                oldItem.dateTime == newItem.dateTime &&
                oldItem.imageUrl == newItem.imageUrl
    }
}