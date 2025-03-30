package com.talhaatif.tickojet

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.stripe.android.paymentsheet.PaymentSheet
import com.talhaatif.tickojet.adapter.SeatAdapter
import com.talhaatif.tickojet.bottomsheets.PaymentBottomSheet
import com.talhaatif.tickojet.databinding.ActivityBookSeatsBinding
import com.talhaatif.tickojet.local.TokenManager
import com.talhaatif.tickojet.repository.EventRepository
import com.talhaatif.tickojet.responseModel.Seat
import com.talhaatif.tickojet.utils.LoadingDialog
import com.talhaatif.tickojet.utils.Result
import com.talhaatif.tickojet.viewmodel.EventViewModel
import com.talhaatif.tickojet.viewmodel.factory.EventViewModelFactory

import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.talhaatif.tickojet.responseModel.StripeIntentResponse

class SeatSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookSeatsBinding
    private lateinit var eventViewModel: EventViewModel
    private lateinit var seatAdapter: SeatAdapter
    private var totalPrice = 0.0
    private var eventId : String? = null

    private lateinit var paymentSheet: PaymentSheet
    private var stripePaymentIntentId: String? = null
    private var selectedSeatsForPayment: List<Seat> = emptyList()
    private var eventIdForPayment: String? = null



    private val loadingDialog by lazy { LoadingDialog(this) }  // Initialize Custom Loader


    // In your activity
    override fun onPause() {
        super.onPause()
        if (loadingDialog.isShowing()) {
            loadingDialog.dismiss()
        }
    }

    private fun initializeStripe() {
        try {
            paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
            Log.d("Stripe", "PaymentSheet initialized successfully")
        } catch (e: Exception) {
            Log.e("Stripe", "PaymentSheet initialization failed", e)
            runOnUiThread {
                Toast.makeText(
                    this,
                    "Payment system unavailable. Please try again later.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    fun setUpViewMode(){
        // Initialize ViewModel
        val tokenManager = TokenManager(this)
        val eventRepository = EventRepository(tokenManager)
        eventViewModel = ViewModelProvider(this,
            EventViewModelFactory(eventRepository, tokenManager, this))
            .get(EventViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBookSeatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Stripe PaymentSheet with logging
        initializeStripe()

        // Initialize token manager and view model
        setUpViewMode()



        // Setup RecyclerView
        seatAdapter = SeatAdapter(this, emptyList()) { seat, isSelected ->
            updateTotalPrice(seat, isSelected)
        }
        binding.recyclerSeats.adapter = seatAdapter
        binding.recyclerSeats.layoutManager = GridLayoutManager(this, 4)

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
                startStripePayment(selectedSeats)

                Toast.makeText(this, "Stripe Payment Selected", Toast.LENGTH_SHORT).show()
                // Handle Stripe payment here
            }
        )
        paymentBottomSheet.show(supportFragmentManager, "PaymentBottomSheet")
    }


    private fun startStripePayment(selectedSeats: List<Seat>) {
        if (selectedSeats.isEmpty()) {
            Toast.makeText(this, "No seats selected", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("StripeFlow", "Starting Stripe payment process")
        loadingDialog.show()
        binding.buyButton.isEnabled = false
        selectedSeatsForPayment = selectedSeats
        eventIdForPayment = eventId

        // Clear previous observers to avoid multiple calls
        eventViewModel.stripeIntent.removeObservers(this)

        eventId?.let { eventId ->
            val seatNumbers = selectedSeats.map { it.seatNumber }
            Log.d("StripeFlow", "Creating payment intent for seats: $seatNumbers")
            eventViewModel.createStripePaymentIntent(eventId, seatNumbers)
        } ?: run {
            Log.e("StripeFlow", "Event ID is null")
            loadingDialog.dismiss()
            binding.buyButton.isEnabled = true
            Toast.makeText(this, "Event information missing", Toast.LENGTH_LONG).show()
        }

        // Observe response
        eventViewModel.stripeIntent.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    Log.d("StripeFlow", "Payment intent received")
                    setupStripePayment(result.data)
                }
                is Result.Error -> {
                    loadingDialog.dismiss()
                    binding.buyButton.isEnabled = true
                    Log.e("StripeFlow", "Error: ${result.message}")
                    Toast.makeText(
                        this,
                        "Payment setup failed: ${result.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                Result.Loading -> { /* Loading state handled by dialog */ }
            }
        }
    }


    private fun setupStripePayment(intentResponse: StripeIntentResponse) {
        Log.d("StripeFlow", "Setting up Stripe payment with configuration")

        try {

            // 1. Dismiss loading dialog first
            if (loadingDialog.isShowing()) {
                loadingDialog.dismiss()
                Log.d("StripeFlow", "Dismissed loading dialog")
            }

            stripePaymentIntentId = extractPaymentIntentId(intentResponse.clientSecret)
            Log.d("StripeFlow", "PaymentIntent ID: $stripePaymentIntentId")

            // Ensure we're on UI thread and activity is valid
            runOnUiThread {
                if (isFinishing || isDestroyed) {
                    Log.e("StripeFlow", "Activity is finishing or destroyed")
                    return@runOnUiThread
                }

                try {
                    PaymentConfiguration.init(
                        this,
                        intentResponse.publishableKey
                    )
                    Log.d("StripeConfig", "Stripe initialized with publishable key")

                    val configuration = PaymentSheet.Configuration(
                        merchantDisplayName = "TickoJet",
                        customer = PaymentSheet.CustomerConfiguration(
                            intentResponse.customerId,
                            intentResponse.ephemeralKey
                        )
                    )

                    Log.d("StripeConfig", "Presenting PaymentSheet...")
                    paymentSheet.presentWithPaymentIntent(
                        intentResponse.clientSecret,
                        configuration
                    )
                    Log.d("StripeFlow", "PaymentSheet presentation initiated")
                } catch (e: Exception) {
                    Log.e("StripeFlow", "Error presenting PaymentSheet", e)
                    Toast.makeText(
                        this,
                        "Payment error: ${e.message ?: "Unknown error"}",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.buyButton.isEnabled = true
                }
            }
        } catch (e: Exception) {
            Log.e("StripeFlow", "Error in setupStripePayment", e)
            runOnUiThread {
                Toast.makeText(
                    this,
                    "Payment setup failed: ${e.message ?: "Unknown error"}",
                    Toast.LENGTH_LONG
                ).show()
                binding.buyButton.isEnabled = true
            }
        }
    }

    private fun onPaymentSheetResult(paymentResult: PaymentSheetResult) {
        when (paymentResult) {
            is PaymentSheetResult.Completed -> {
                Log.d("StripeResult", "Payment completed successfully")
                Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show()

                stripePaymentIntentId?.let { paymentIntentId ->
                    eventIdForPayment?.let { eventId ->
                        val seatNumbers = selectedSeatsForPayment.map { it.seatNumber }
                        Log.d("StripeFlow", "Confirming booking with payment ID: $paymentIntentId")
                        eventViewModel.confirmStripeBooking(
                            paymentIntentId,
                            eventId,
                            seatNumbers
                        )
                    } ?: Log.e("StripeFlow", "Event ID is null when confirming booking")
                } ?: Log.e("StripeFlow", "Payment Intent ID is null when confirming booking")
            }
            is PaymentSheetResult.Canceled -> {
                Log.d("StripeResult", "Payment canceled by user")
                Toast.makeText(this, "Payment canceled", Toast.LENGTH_SHORT).show()
            }
            is PaymentSheetResult.Failed -> {
                Log.e("StripeResult", "Payment failed: ${paymentResult.error.message}")
                Toast.makeText(this, "Payment failed: ${paymentResult.error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Add this to your activity or a utility class
    private fun extractPaymentIntentId(clientSecret: String): String? {
        return clientSecret.split("_secret_").firstOrNull()
    }

    private fun updateSeatStatus(seatNumbers: List<String>, status: String) {
        seatAdapter.updateSeatStatus(seatNumbers, status)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clear observers to prevent leaks
        eventViewModel.stripeIntent.removeObservers(this)
        eventViewModel.bookingState.removeObservers(this)
    }


}