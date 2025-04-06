package com.talhaatif.tickojet.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.talhaatif.tickojet.R
import com.talhaatif.tickojet.adapter.SearchEventsAdapter
import com.talhaatif.tickojet.databinding.FragmentSearchBinding
import com.talhaatif.tickojet.databinding.BottomSheetFilterEventsBinding
import com.talhaatif.tickojet.local.TokenManager
import com.talhaatif.tickojet.repository.SearchRepository
import com.talhaatif.tickojet.responseModel.Event
import com.talhaatif.tickojet.utils.Category
import com.talhaatif.tickojet.utils.Result
import com.talhaatif.tickojet.viewmodel.SearchViewModel
import com.talhaatif.tickojet.viewmodel.factory.SearchViewModelFactory


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var tokenManager: TokenManager
    private lateinit var adapter: SearchEventsAdapter
    private var selectedCategory: String? = null

    private val viewModel: SearchViewModel by viewModels {
        SearchViewModelFactory(
            SearchRepository(tokenManager),
            tokenManager,
            requireContext()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        tokenManager = TokenManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        setupFilterButton()
        setupObservers()
    }

    private fun setupRecyclerView() {
        adapter = SearchEventsAdapter { event ->

        }

        binding.userSearchedEventsHolder.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SearchFragment.adapter
        }
    }

    private fun setupSearch() {
        binding.etSearch.doAfterTextChanged { text ->
            // Implement search functionality if needed
        }
    }

    private fun setupFilterButton() {
        binding.btnFilter.setOnClickListener {
            showFilterBottomSheet()
        }
    }

    private fun setupObservers() {
        viewModel.eventsByCategory.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> showLoading()
                is Result.Success -> showEvents(result.data)
                is Result.Error -> showError(result.message)
            }
        }
    }

    private fun showFilterBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetBinding = BottomSheetFilterEventsBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bottomSheetBinding.root)

        // Configure ChipGroup for single selection
        bottomSheetBinding.categoryChipGroup.isSingleSelection = true

        // Clear any existing chips (in case we're reusing the layout)
        bottomSheetBinding.categoryChipGroup.removeAllViews()

        // Create chips for all categories
        val categories = Category.values()
        categories.forEach { category ->
            val chip = Chip(requireContext()).apply {
                id = View.generateViewId()
                text = category.name.replaceFirstChar { it.uppercase() }
                isCheckable = true
                isChecked = category.name == selectedCategory
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedCategory = category.name
                    }
                }
            }
            bottomSheetBinding.categoryChipGroup.addView(chip)
        }

        bottomSheetDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bottomSheetBinding.priceRangeSlider.addOnChangeListener { slider, value, fromUser ->
            // Handle price range changes if needed
        }

        bottomSheetBinding.applyButton.setOnClickListener {
            selectedCategory?.let { category ->
                viewModel.getEventsByCategory(category)
            } ?: run {
                Toast.makeText(requireContext(), "Please select a category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            bottomSheetDialog.dismiss()
        }

        bottomSheetBinding.resetButton.setOnClickListener {
            bottomSheetBinding.categoryChipGroup.clearCheck()
            bottomSheetBinding.priceRangeSlider.values = listOf(0f, 100f)
            selectedCategory = null
            viewModel.getEventsByCategory("") // Load all events
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }
    private fun showLoading() {
        binding.userSearchedEventsHolder.showLoading()
    }

    private fun showEvents(events: List<Event>) {
        if (events.isEmpty()) {
            binding.userSearchedEventsHolder.showEmpty()
        } else {
            binding.userSearchedEventsHolder.showContent()
            adapter.submitList(events)
        }
    }

    private fun showError(message: String) {
        binding.userSearchedEventsHolder.showError()
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}