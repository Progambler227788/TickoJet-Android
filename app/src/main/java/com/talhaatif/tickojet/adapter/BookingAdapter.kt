package com.talhaatif.tickojet.adapter

import android.content.Context
import android.content.res.ColorStateList
import com.talhaatif.tickojet.responseModel.Booking
import com.talhaatif.tickojet.responseModel.SimplifiedTrendingEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.talhaatif.tickojet.R
import com.talhaatif.tickojet.databinding.RvBookingBinding
import com.talhaatif.tickojet.responseModel.BookingStatus
import com.talhaatif.tickojet.utils.DateUtils
import java.util.Random


class BookingAdapter(
    private val context: Context,
    private var bookings: List<Booking>,
    private val onItemClick: (Booking) -> Unit = {}
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    inner class BookingViewHolder(private val binding: RvBookingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(booking: Booking) {
            with(binding) {
                // Format dates
                val bookingDate = DateUtils.formatEventDate(booking.createdAt)
                val eventDate = DateUtils.formatEventDate(booking.eventDate)

                // Set text values
                tvBookingDate.text = "Booked on $bookingDate"
                tvBookingId.text = "Booking #${booking.id.substring(0, 8)}"
                tvTicketQuantity.text = "${booking.seats.size} tickets"
                tvEventName.text = booking.eventName
                tvEventDate.text = eventDate
                tvAmountPrice.text = "Rs. ${"%.2f".format(booking.payment.amount)}"

                // Handle booking status
                when (booking.status) {
                    BookingStatus.CONFIRMED -> {
                        btnStatus.text = root.context.getString(R.string.status_confirmed)
                        btnStatus.setTextColor(ContextCompat.getColor(root.context, R.color.success_green))
                        btnStatus.backgroundTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(root.context, R.color.light_green)
                        )
                    }
                    BookingStatus.CANCELLED -> {
                        btnStatus.text = root.context.getString(R.string.status_cancelled)
                        btnStatus.setTextColor(ContextCompat.getColor(root.context, R.color.error_red))
                        btnStatus.backgroundTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(root.context, R.color.light_red)
                        )
                    }
                    BookingStatus.PENDING -> {
                        btnStatus.text = root.context.getString(R.string.status_pending)
                        btnStatus.setTextColor(ContextCompat.getColor(root.context, R.color.status_pending))
                        btnStatus.backgroundTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(root.context, R.color.status_pending)
                        )
                    }
                }



                val drawables = listOf(R.drawable.a4, R.drawable.a5, R.drawable.a6)
                Glide.with(root.context)
                    .load(drawables.random())
                    .placeholder(R.drawable.a1)
                    .into(ivEventIcon)

                // Set click listener
                root.setOnClickListener { onItemClick(booking) }
            }
        }
    }

    fun submitList(newBookings: List<Booking>) {
        // Implement proper diffing if needed
        bookings = newBookings
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = RvBookingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(bookings[position])
    }

    override fun getItemCount(): Int = bookings.size
}