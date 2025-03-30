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

                // Handle category chip
                event.category?.let { category ->
                    chipCategory.text = category
                    // You can add category-specific styling here
                } ?: run { chipCategory.visibility = View.GONE }


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