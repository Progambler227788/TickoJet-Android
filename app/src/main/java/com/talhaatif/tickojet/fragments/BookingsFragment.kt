package com.talhaatif.tickojet.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.talhaatif.tickojet.adapter.BookingAdapter
import com.talhaatif.tickojet.databinding.FragmentBookingsBinding
import com.talhaatif.tickojet.databinding.FragmentDashBoardBinding
import com.talhaatif.tickojet.local.TokenManager
import com.talhaatif.tickojet.repository.BookingRepository
import com.talhaatif.tickojet.utils.Result
import com.talhaatif.tickojet.viewmodel.BookingViewModel
import com.talhaatif.tickojet.viewmodel.factory.BookingViewModelFactory


class BookingsFragment : Fragment() {

    private lateinit var tokenManager: TokenManager
    private lateinit var bookingAdapter: BookingAdapter

    private var _binding: FragmentBookingsBinding? = null
    private val binding get() = _binding!!

    private val bookingViewModel: BookingViewModel by viewModels {
        BookingViewModelFactory(
            BookingRepository(tokenManager),
            tokenManager,
            requireContext()
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBookingsBinding.inflate(inflater, container, false)
        tokenManager = TokenManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadBookings()
    }

    private fun setupRecyclerView() {
        bookingAdapter = BookingAdapter(requireContext(), emptyList())
        binding.userBookingsRecyclerView.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bookingAdapter
        }
    }

    private fun loadBookings() {
        binding.userBookingsRecyclerView.showLoading()
        bookingViewModel.bookings.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> binding.userBookingsRecyclerView.showLoading()
                is Result.Success -> {
                    if (result.data.isEmpty()) {
                        binding.userBookingsRecyclerView.showEmpty()
                    } else {
                        bookingAdapter.submitList(result.data)
                        binding.userBookingsRecyclerView.showContent()
                    }
                }
                is Result.Error -> {
                    binding.userBookingsRecyclerView.showError()
                    // Show error message
                }
            }
        }
        bookingViewModel.getBookings()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}