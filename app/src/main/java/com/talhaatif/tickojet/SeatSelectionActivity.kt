package com.talhaatif.tickojet

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.talhaatif.tickojet.adapter.SeatAdapter
import com.talhaatif.tickojet.bottomsheets.PaymentBottomSheet
import com.talhaatif.tickojet.data.remote.client.WebSocketClient
import com.talhaatif.tickojet.databinding.ActivityBookSeatsBinding
import com.talhaatif.tickojet.local.TokenManager
import com.talhaatif.tickojet.repository.EventRepository
import com.talhaatif.tickojet.responseModel.Seat
import com.talhaatif.tickojet.utils.LoadingDialog
import com.talhaatif.tickojet.utils.Result
import com.talhaatif.tickojet.viewmodel.EventViewModel
import com.talhaatif.tickojet.viewmodel.factory.EventViewModelFactory

class SeatSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookSeatsBinding
    private lateinit var eventViewModel: EventViewModel
    private lateinit var seatAdapter: SeatAdapter
    private lateinit var webSocketClient: WebSocketClient
    private var totalPrice = 0.0
    private var eventId : String? = null


    private val loadingDialog by lazy { LoadingDialog(this) }  // Initialize Custom Loader

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
                // use dialog here
                proceedToPayment(selectedSeats)
            }
        }

        // Fetch event details
         eventId = intent.getStringExtra("EVENT_ID") ?: run {
            finish()
            return
        }

        // Initialize WebSocket
        eventId?.let { eventId ->
            webSocketClient = WebSocketClient(eventId) { seatNumbers, status ->
                runOnUiThread {
                    updateSeatStatus(seatNumbers, status)
                }
            }
            webSocketClient.connect()
        }

        // Observe booking state
        eventViewModel.bookingState.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    // Show loading indicator
                    loadingDialog.show()

                    binding.buyButton.isEnabled = false
                    binding.buyButton.text = "Processing..."
                }
                is Result.Success -> {
                    // Handle successful booking
                    eventViewModel.getEventById(eventId!!)
                    binding.buyButton.isEnabled = true
                    totalPrice = 0.0
                    binding.totalPriceText.text = "Total: Rs ${"%.2f".format(totalPrice)}"
                    binding.buyButton.text = "Buy Tickets"
                    Toast.makeText(this, result.data.message, Toast.LENGTH_SHORT).show()
                    // Optionally navigate to booking confirmation
                }
                is Result.Error -> {
                    // Show error message
                    loadingDialog.dismiss()
                    binding.buyButton.isEnabled = true
                    binding.buyButton.text = "Buy Tickets"
                    Log.d("Error in Seat Selection",result.message)
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        fetchEventDetails(eventId!!)
    }

    private fun fetchEventDetails(eventId: String) {
        eventViewModel.eventDetails.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    loadingDialog.dismiss()

                    val event = result.data
                    seatAdapter = SeatAdapter(this, event.seats) { seat, isSelected ->
                        updateTotalPrice(seat, isSelected)
                    }
                    binding.recyclerSeats.adapter = seatAdapter
                }
                is Result.Error -> {
                    loadingDialog.dismiss()
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
        val paymentBottomSheet = PaymentBottomSheet(
            onWalletPay = {
                Toast.makeText(this, "Wallet Payment Selected", Toast.LENGTH_SHORT).show()
                // Handle wallet payment here
                eventId?.let { eventId ->
                    val seatNumbers = selectedSeats.map { it.seatNumber }
                    eventViewModel.bookWithWallet(eventId, seatNumbers)
                }
            },
            onStripePay = {
                Toast.makeText(this, "Stripe Payment Selected", Toast.LENGTH_SHORT).show()
                // Handle Stripe payment here
            }
        )
        paymentBottomSheet.show(supportFragmentManager, "PaymentBottomSheet")
    }


    private fun updateSeatStatus(seatNumbers: List<String>, status: String) {
        seatAdapter.updateSeatStatus(seatNumbers, status)
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocketClient.disconnect()
    }
}