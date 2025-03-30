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
import com.talhaatif.tickojet.databinding.RvEventFeaturedBinding
import com.talhaatif.tickojet.responseModel.SimplifiedTrendingEvent
import com.talhaatif.tickojet.utils.DateUtils

// TrendingEventsAdapter.kt
class TrendingEventsAdapter(
    private val onFavoriteClick: (SimplifiedTrendingEvent) -> Unit
) : ListAdapter<SimplifiedTrendingEvent, TrendingEventsAdapter.EventViewHolder>(TrendingDiffCallback()) {

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


            // Load image with Glide or Picasso
            val drawables = listOf(R.drawable.a1,R.drawable.a4, R.drawable.a5, R.drawable.event1)
            event.imageUrl?.let {
                Glide.with(binding.root.context)
                    .load(drawables.random())
                    .placeholder(R.drawable.a3) // placeholder image
                    .into(binding.eventImage)
            } ?: run {
                binding.eventImage.setImageResource(drawables.random())
            }

            // Use hardware layers for animated views
            binding.eventImage.setLayerType(View.LAYER_TYPE_HARDWARE, null)

            binding.eventFavorite.setOnClickListener {
                onFavoriteClick(event)
            }
        }
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
        holder.bind(getItem(position))
    }


}

private class TrendingDiffCallback : DiffUtil.ItemCallback<SimplifiedTrendingEvent>() {
    override fun areItemsTheSame(oldItem: SimplifiedTrendingEvent, newItem: SimplifiedTrendingEvent): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SimplifiedTrendingEvent, newItem: SimplifiedTrendingEvent): Boolean {
        return oldItem == newItem
    }
}