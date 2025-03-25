package com.talhaatif.tickojet.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.talhaatif.tickojet.R
import com.talhaatif.tickojet.adapter.TrendingEventsAdapter
import com.talhaatif.tickojet.databinding.FragmentDashBoardBinding
import com.talhaatif.tickojet.local.TokenManager
import com.talhaatif.tickojet.repository.EventRepository
import com.talhaatif.tickojet.requestModel.SimplifiedTrendingEvent
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

        eventViewModel.getTrendingEvents()

//        setupRecyclerView()
        setupTrendingEvents()


//        loadTrendingEvents()



        animateProfileImage()
        customizeProfileImage()
    }

    private fun setupRecyclerView() {
        binding.trendingEventsView.recyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
    }
    private fun setupTrendingEvents() {
        binding.trendingEventsView.apply {
            setLayoutManager(LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            ))

            // Enable modern carousel effect
            enableCarouselEffect(scale = 0.85f)

            // Set up animations
            setupRecyclerViewAnimations()

            // Set retry action
            retryAction = { loadTrendingEvents() }
        }

        loadTrendingEvents()
    }


    private fun loadTrendingEvents() {
        binding.trendingEventsView.showLoading()
        eventViewModel.trendingEvents.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> binding.trendingEventsView.showLoading()
                is Result.Success -> {
                    if (result.data.isEmpty()) {
                        binding.trendingEventsView.showEmpty()
                    } else {
                        // Initialize adapter first if null
                        if (binding.trendingEventsView.recyclerView.adapter == null) {
                            binding.trendingEventsView.recyclerView.adapter = TrendingEventsAdapter(
                                result.data,
                                { event -> /* Handle favorite click */ }
                            )
                        } else {
                            // Update existing adapter data
                            (binding.trendingEventsView.recyclerView.adapter as TrendingEventsAdapter)
                                .submitList(result.data)
                        }
                        binding.trendingEventsView.showContent()
                    }
                }
                is Result.Error -> binding.trendingEventsView.showError()
            }
        }
    }

    private fun observeTrendingEvents() {
        eventViewModel.trendingEvents.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    // Show loading indicator if needed
                }
                is Result.Success -> {
                    val adapter = TrendingEventsAdapter(result.data) { event ->
                        // Handle favorite click
                    }
//                    binding.rvUpcomingEvents.adapter = adapter
                }
                is Result.Error -> {
                    Log.d("Error in DashBoardFragment", "Error: ${result.message}")
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
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