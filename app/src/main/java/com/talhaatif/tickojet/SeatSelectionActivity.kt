package com.talhaatif.tickojet

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.talhaatif.tickojet.adapter.SeatAdapter
import com.talhaatif.tickojet.databinding.ActivityBookSeatsBinding
import com.talhaatif.tickojet.local.TokenManager
import com.talhaatif.tickojet.repository.EventRepository
import com.talhaatif.tickojet.responseModel.Seat
import com.talhaatif.tickojet.utils.Result
import com.talhaatif.tickojet.viewmodel.EventViewModel
import com.talhaatif.tickojet.viewmodel.factory.EventViewModelFactory

class SeatSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookSeatsBinding
    private lateinit var eventViewModel: EventViewModel
    private lateinit var seatAdapter: SeatAdapter
    private var totalPrice = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBookSeatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        val tokenManager = TokenManager(this)
        val eventRepository = EventRepository(tokenManager)
        eventViewModel = ViewModelProvider(this,
            EventViewModelFactory(eventRepository, tokenManager, this))
            .get(EventViewModel::class.java)

        // Setup RecyclerView
        seatAdapter = SeatAdapter(this, emptyList()) { seat, isSelected ->
            updateTotalPrice(seat, isSelected)
        }
        binding.recyclerSeats.adapter = seatAdapter
        binding.recyclerSeats.layoutManager = GridLayoutManager(this, 6)

        // Handle buy button click
        binding.buyButton.setOnClickListener {
            val selectedSeats = seatAdapter.getSelectedSeats()
            if (selectedSeats.isEmpty()) {
                Toast.makeText(this, "Please select at least one seat", Toast.LENGTH_SHORT).show()
            } else {
                // Proceed to payment
                proceedToPayment(selectedSeats)
            }
        }

        // Fetch event details
        val eventId = intent.getStringExtra("EVENT_ID") ?: run {
            finish()
            return
        }
        fetchEventDetails(eventId)
    }

    private fun fetchEventDetails(eventId: String) {
        eventViewModel.eventDetails.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    val event = result.data
                    seatAdapter = SeatAdapter(this, event.seats) { seat, isSelected ->
                        updateTotalPrice(seat, isSelected)
                    }
                    binding.recyclerSeats.adapter = seatAdapter
                }
                is Result.Error -> {
                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_LONG).show()
                }
                Result.Loading -> {
                    // Show loading indicator if needed
                }
            }
        }
        eventViewModel.getEventById(eventId)
    }

    private fun updateTotalPrice(seat: Seat, isSelected: Boolean) {
        totalPrice = if (isSelected) {
            totalPrice + seat.price
        } else {
            totalPrice - seat.price
        }
        binding.totalPriceText.text = "Total: Rs ${"%.2f".format(totalPrice)}"
    }

    private fun proceedToPayment(selectedSeats: List<Seat>) {
//        // Create intent to payment activity
//        val intent = Intent(this, PaymentActivity::class.java).apply {
//            putExtra("TOTAL_PRICE", totalPrice)
//            putStringArrayListExtra("SELECTED_SEATS",
//                selectedSeats.map { it.seatNumber } as ArrayList<String>)
//            // Add any other necessary data
//        }
//        startActivity(intent)
    }
}