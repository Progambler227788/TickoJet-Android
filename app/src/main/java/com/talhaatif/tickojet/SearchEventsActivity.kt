package com.talhaatif.tickojet

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import  com.talhaatif.tickojet.utils.Result
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.talhaatif.tickojet.adapter.SearchEventsAdapter
import com.talhaatif.tickojet.databinding.ActivitySearchEventsBinding
import com.talhaatif.tickojet.databinding.BottomSheetFilterEventsBinding
import com.talhaatif.tickojet.local.TokenManager
import com.talhaatif.tickojet.repository.SearchRepository
import com.talhaatif.tickojet.responseModel.Event
import com.talhaatif.tickojet.viewmodel.SearchViewModel
import com.talhaatif.tickojet.viewmodel.factory.SearchViewModelFactory

class SearchEventsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchEventsBinding
    private lateinit var viewModel: SearchViewModel
    private lateinit var adapter: SearchEventsAdapter
    private var selectedCategory: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        setupViewModel()
        setupRecyclerView()
        setupSearch()
        setupFilterButton()
    }

    private fun setupViewModel() {
        val tokenManager = TokenManager(this)
        val searchRepository = SearchRepository(tokenManager)
        val factory = SearchViewModelFactory(searchRepository, tokenManager, this)
        viewModel = ViewModelProvider(this, factory)[SearchViewModel::class.java]

        viewModel.eventsByCategory.observe(this, Observer { result ->
            result?.let {

                when (result) {
                    is Result.Loading -> showLoading()
                    is Result.Success -> showEvents(result.data)
                    is Result.Error -> showError(result.message)
                }

            }
        })
    }

    private fun setupRecyclerView() {
        adapter = SearchEventsAdapter { event ->
            // Handle event click
            startActivity(Intent(this, BookEventActivity::class.java).apply {
                putExtra("event_id", event.id)
            })
        }

        binding.userSearchedEventsHolder.recyclerView.apply {
            layoutManager =  LinearLayoutManager(this@SearchEventsActivity)
            adapter = this@SearchEventsActivity.adapter
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

    private fun showFilterBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetBinding = BottomSheetFilterEventsBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bottomSheetBinding.root)

        // Set the background to transparent for rounded corners
        bottomSheetDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Set up chip group
        bottomSheetBinding.categoryChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            selectedCategory = when {
                checkedIds.contains(R.id.chip_concert) -> "Concert"
                checkedIds.contains(R.id.chip_sports) -> "Sports"
                checkedIds.contains(R.id.chip_theater) -> "Theater"
                checkedIds.contains(R.id.chip_festival) -> "Festival"
                else -> null
            }
        }

        // Set up price range slider
        bottomSheetBinding.priceRangeSlider.addOnChangeListener { slider, value, fromUser ->
            // Handle price range changes if needed
        }

        // Apply button click
        bottomSheetBinding.applyButton.setOnClickListener {
            selectedCategory?.let { category ->
                viewModel.getEventsByCategory(category)
            } ?: run {
                Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            bottomSheetDialog.dismiss()
        }

        // Reset button click
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}