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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = RvSearchedEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventViewHolder(binding)
    }

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

                Glide.with(itemView)
                    .load(event.imageUrl)
                    .placeholder(R.drawable.a2)
                    .into(eventImage)

                itemView.setOnClickListener { onClick(event) }
            }
        }
    }

    class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Event, newItem: Event) = oldItem == newItem
    }
}