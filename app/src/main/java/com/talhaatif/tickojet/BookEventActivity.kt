package com.talhaatif.tickojet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.talhaatif.tickojet.databinding.ActivityBookEventBinding
import com.talhaatif.tickojet.viewmodel.EventViewModel
import  com.talhaatif.tickojet.utils.Result
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.talhaatif.tickojet.adapter.viewholder.ImageBannerViewHolder
import com.talhaatif.tickojet.repository.EventRepository
import com.talhaatif.tickojet.local.TokenManager
import com.talhaatif.tickojet.utils.CategoryStyleUtils
import com.talhaatif.tickojet.viewmodel.factory.EventViewModelFactory

class BookEventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookEventBinding
    private lateinit var eventViewModel: EventViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Initialize ViewModel with dependencies

        val tokenManager = TokenManager(this)
        val eventRepository = EventRepository(tokenManager)
        eventViewModel = ViewModelProvider(this, EventViewModelFactory(eventRepository, tokenManager, this))
            .get(EventViewModel::class.java)


        val eventId = intent.getStringExtra("EVENT_ID") ?: return
        Log.d("Book Event Activity", "Event ID From Local: ${eventId}")
        fetchEventDetails(eventId)

        binding.btnBookTickets.setOnClickListener {
            val intent = Intent(this, SeatSelectionActivity::class.java)
            intent.putExtra("EVENT_ID", eventId)
            startActivity(intent)
        }

//        setupImageSlider()

//        setupImageSliderWithGlide()

        setupImageSliderWithBannerViewPager()

    }

    private fun fetchEventDetails(eventId: String) {

        Log.d("BookEventActivity", "Fetching details for event: $eventId")

        eventViewModel.getEventById(eventId)


        eventViewModel.eventDetails.observe(this, Observer { result ->
            result?.let {
                when (it) {
                    is Result.Success -> {

                        Log.d("Book Event Activity", "Event Details: ${it.data.id}")
                        val event = it.data
                        binding.tvEventName.text = event.title

                        binding.btnEventCategory. apply {
                            CategoryStyleUtils.applyCategoryStyle(this, event.category)
                            text = event.category
                        }

                        binding.btnAddToCalendar.text = "Add this to My Calendar"
                        binding.tvLocation.text = event.location
                        binding.tvFullLocation.text = event.location
//                        binding.tvEventDescription.text = event.description
                        // Update other views as needed
                    }
                    is Result.Error -> {
                        // Handle error case
                        Log.e("BookEventActivity", "Error fetching event: ${it.message}")
                    }
                    is Result.Loading -> {
                        Log.d("BookEventActivity", "Loading event details...")
                        // Handle loading state if needed
                    }
                }
            }
        })
        // Trigger the actual fetch
        eventViewModel.getEventById(eventId)
    }


    private fun setupImageSliderWithBannerViewPager() {
        val imageResources = listOf(R.drawable.a1, R.drawable.a2, R.drawable.a3)

        binding.imageSlider
            .setAdapter(ImageBannerViewHolder())
            .create(imageResources)
    }



//    private fun setupImageSliderWithGlide() {
//        val imageResources = listOf(R.drawable.a1, R.drawable.a1, R.drawable.a1)
//        val slideModels = ArrayList<SlideModel>()
//
//        imageResources.forEach { imageRes ->
//            // Pre-load with Glide (optional transformations)
//            Glide.with(this)
//                .load(imageRes)
//                .override(800, 800)  // Downscale if needed
//                .preload()  // Cache the image
//
//            // Add to slider
//            slideModels.add(
//                SlideModel(
//                    imageRes,  // Still pass the resource ID
//                    scaleType = ScaleTypes.CENTER_CROP
//                )
//            )
//        }
//
//        binding.imageSlider.setImageList(slideModels)
//    }



}
// Notes for remembering

/*
*        /*
        * How Observer Works?
Observes eventViewModel.eventDetails (LiveData<Result<T>>).

Whenever _trendingEvents in ViewModel changes, this observer gets triggered.

Based on the Result type, UI updates accordingly.
* */ */