package com.talhaatif.tickojet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.talhaatif.tickojet.R
import com.talhaatif.tickojet.adapter.TrendingEventsAdapter
import com.talhaatif.tickojet.adapter.UpcomingEventsAdapter
import com.talhaatif.tickojet.databinding.FragmentDashBoardBinding
import com.talhaatif.tickojet.local.TokenManager
import com.talhaatif.tickojet.repository.EventRepository
import com.talhaatif.tickojet.utils.Result
import com.talhaatif.tickojet.viewmodel.EventViewModel
import com.talhaatif.tickojet.viewmodel.factory.EventViewModelFactory

class DashBoardFragment : Fragment() {
    private lateinit var tokenManager: TokenManager
    private val eventRepository by lazy { EventRepository(tokenManager) }

    // Changed from activity.viewModels() to fragment.viewModels()
    private val eventViewModel: EventViewModel by viewModels {
        EventViewModelFactory(eventRepository, tokenManager, requireContext())
    }

    private var _binding: FragmentDashBoardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashBoardBinding.inflate(inflater, container, false)
        tokenManager = TokenManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize adapters first
        binding.trendingEventsView.recyclerView.adapter = TrendingEventsAdapter { event ->
            // Handle trending event click
        }

        binding.upcomingEventsView.recyclerView.adapter = UpcomingEventsAdapter()

        // Setup layouts
        setupTrendingEvents()
        setupUpcomingEvents()

        // Animate profile image
        animateProfileImage()
        customizeProfileImage()

        // Load data
        eventViewModel.getTrendingEvents()
        eventViewModel.getUpcomingEvents()
    }

    override fun onStart() {
        super.onStart()
        setupObservers()
    }
    private fun setupTrendingEvents() {
        binding.trendingEventsView.apply {
            // 1. Setup layout manager with pre-caching
            val layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            ).apply {
                initialPrefetchItemCount = 5 // Pre-load items for smooth scrolling
            }
            setLayoutManager(layoutManager)

            // 2. Enable optimized carousel
            enableSmoothCarouselEffect(
                scale = 0.85f,
                elevation = 12f
            )

            // 3. Setup smooth animations
            setupSmoothAnimations()

            // 4. Set retry action
            retryAction = { loadTrendingEvents() }

            // 5. Enable hardware acceleration
            recyclerView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        }
    }


    private fun setupObservers() {
        eventViewModel.trendingEvents.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> binding.trendingEventsView.showLoading()
                is Result.Success -> {
                    (binding.trendingEventsView.recyclerView.adapter as TrendingEventsAdapter)
                        .submitList(result.data)
                    binding.trendingEventsView.showContent()
                }
                is Result.Error -> {
                    binding.trendingEventsView.showError()
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        eventViewModel.upcomingEvents.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> binding.upcomingEventsView.showLoading()
                is Result.Success -> {
                    (binding.upcomingEventsView.recyclerView.adapter as UpcomingEventsAdapter)
                        .submitList(result.data)
                    binding.upcomingEventsView.showContent()
                }
                is Result.Error -> {
                    binding.upcomingEventsView.showError()
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupUpcomingEvents() {
        binding.upcomingEventsView.apply {
            val layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            ).apply {
                initialPrefetchItemCount = 3 // Pre-load items for smooth scrolling
            }
            setLayoutManager(layoutManager)

            //  Setup smooth animations
            setupSmoothAnimations()


            // Set retry action
            retryAction = { loadUpcomingEvents() }
        }



    }


    private fun loadUpcomingEvents () {
        eventViewModel.getUpcomingEvents()
    }


    private fun loadTrendingEvents() {
        eventViewModel.getTrendingEvents()
    }

    private fun animateProfileImage() {
        binding.profileImage.scaleX = 1f
        binding.profileImage.scaleY = 1f

        binding.profileImage.animate()
            .scaleX(1.5f)
            .scaleY(1.5f)
            .setDuration(600)
            .withEndAction {
                binding.profileImage.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(600)
                    .start()
            }
            .start()
    }



    private fun customizeProfileImage() {
        binding.profileImage.apply {
            borderColor = ContextCompat.getColor(requireContext(), R.color.white)
            borderWidth = 4
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}