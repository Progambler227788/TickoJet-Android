package com.talhaatif.tickojet.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.talhaatif.tickojet.BookEventActivity
import com.talhaatif.tickojet.R
import com.talhaatif.tickojet.databinding.RvEventFeaturedBinding
import com.talhaatif.tickojet.responseModel.SimplifiedTrendingEvent
import com.talhaatif.tickojet.utils.DateUtils

// TrendingEventsAdapter.kt
class TrendingEventsAdapter(
    private val events: List<SimplifiedTrendingEvent>,
    private val onFavoriteClick: (SimplifiedTrendingEvent) -> Unit
) : RecyclerView.Adapter<TrendingEventsAdapter.EventViewHolder>() {

    inner class EventViewHolder(private val binding: RvEventFeaturedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: SimplifiedTrendingEvent) {
            binding.eventTitle.text = event.title
            binding.eventLocation.text = event.location

            binding.root.setOnClickListener {
                val context = it.context
                val intent = Intent(context, BookEventActivity::class.java)

                intent.putExtra("EVENT_ID", event.id)
                context.startActivity(intent)
            }

            // Format date

            binding.eventDateTime.text = DateUtils.formatEventDate(event.dateTime)
//            val dateFormat = SimpleDateFormat("EEE, MMM d • HH:mm", Locale.getDefault())
//            val dateString = dateFormat.format(Date(event.dateTime)) // Convert Long to Date
//            binding.eventDateTime.text = dateString

            // Load image with Glide or Picasso
            event.imageUrl?.let { url ->
                Glide.with(binding.root.context)
                    .load(url)
                    .placeholder(R.drawable.a4) // placeholder image
                    .into(binding.eventImage)
            } ?: run {
                binding.eventImage.setImageResource(R.drawable.a4)
            }

            binding.eventFavorite.setOnClickListener {
                onFavoriteClick(event)
            }
        }
    }

    fun submitList(newList: List<SimplifiedTrendingEvent>) {
        // Implement proper diffing if needed
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = RvEventFeaturedBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount() = events.size
}