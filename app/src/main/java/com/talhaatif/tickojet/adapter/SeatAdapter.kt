package com.talhaatif.tickojet.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.talhaatif.tickojet.R
import com.talhaatif.tickojet.databinding.SeatItemBinding
import com.talhaatif.tickojet.responseModel.Seat
import com.google.android.material.button.MaterialButton


class SeatAdapter(
    private val context : Context,
    private var seats: List<Seat>,
    private val onSeatSelected: (Seat, Boolean) -> Unit
) : RecyclerView.Adapter<SeatAdapter.SeatViewHolder>() {

    private val selectedSeats = mutableListOf<Seat>()

    @SuppressLint("NotifyDataSetChanged")
    fun updateSeatStatus(seatNumbers: List<String>, status: String) {
        Log.d("SeatAdapter", "Updating seat status: $seatNumbers, $status")

        val isAvailable = status != "BOOKED"

        seats = seats.map { seat ->
            if (seat.seatNumber in seatNumbers) {
                seat.copy(available = isAvailable)
            } else seat
        }

        notifyDataSetChanged() // More efficient than multiple notifyItemChanged calls
    }

    inner class SeatViewHolder(private val binding: SeatItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(seat: Seat) {
            binding.seatButton.text = seat.seatNumber.replace("Seat-", "S-")
            binding.seatButton.isEnabled = seat.available

            // Disable the default state list animator
            binding.seatButton.stateListAnimator = null

            // Set initial appearance
            updateSeatAppearance(seat)

            binding.seatButton.setOnClickListener {
                if (seat.available) {
                    val isSelected = !selectedSeats.contains(seat)
                    if (isSelected) {
                        selectedSeats.add(seat)
                    } else {
                        selectedSeats.remove(seat)
                    }
                    updateSeatAppearance(seat)
                    onSeatSelected(seat, isSelected)
                }
            }
        }

        private fun updateSeatAppearance(seat: Seat) {
            val colorRes = when {
                !seat.available -> R.color.reserved_seat
                selectedSeats.contains(seat) -> R.color.selected_seat
                else -> R.color.available_seat
            }

            binding.seatButton.apply {
                backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, colorRes)
                )
                // Add ripple effect manually
                rippleColor = ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.ripple_color)
                )
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatViewHolder {
        val binding = SeatItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SeatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SeatViewHolder, position: Int) {
        holder.bind(seats[position])
    }

    override fun getItemCount(): Int = seats.size

    fun getSelectedSeats(): List<Seat> = selectedSeats.toList()
}

// Extension function in same file or separate file
private fun MaterialButton.setDefaultClickListenerBehavior(enable: Boolean) {
    if (enable) {
        setOnTouchListener(null)
    } else {
        setOnTouchListener { v, _ ->
            v.performClick()
            true // Consume the touch event
        }
    }
}