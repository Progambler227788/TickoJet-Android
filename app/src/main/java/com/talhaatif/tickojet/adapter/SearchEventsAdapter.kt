package com.talhaatif.tickojet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.talhaatif.tickojet.R
import com.talhaatif.tickojet.databinding.RvEventFeaturedBinding
import com.talhaatif.tickojet.databinding.RvSearchedEventBinding
import com.talhaatif.tickojet.responseModel.Event
import com.talhaatif.tickojet.utils.DateUtils

class SearchEventsAdapter(private val onClick: (Event) -> Unit) :
    ListAdapter<Event, SearchEventsAdapter.EventViewHolder>(EventDiffCallback()) {
    // Inflates the xml layout for me and returns a ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = RvSearchedEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventViewHolder(binding)
    }

    // Binds the data to the ViewHolder

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EventViewHolder(private val binding: RvSearchedEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            with(binding) {
                eventTitle.text = event.title
                eventDateTime.text = DateUtils.formatEventDate(event.dateTime)
                eventLocation.text = event.location
            // Glide is a library that loads images from the internet
                Glide.with(itemView)
                    .load(event.imageUrl)
                    .placeholder(R.drawable.a2)
                    .into(eventImage)

                itemView.setOnClickListener { onClick(event) }
            }
        }
    }

    // To avoid loading items that has not been changed
    class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
        // compares the item by id
        override fun areItemsTheSame(oldItem: Event, newItem: Event) = oldItem.id == newItem.id
        // compares the item by their content like internal attributes title, location etc
        override fun areContentsTheSame(oldItem: Event, newItem: Event) = oldItem == newItem
    }
}