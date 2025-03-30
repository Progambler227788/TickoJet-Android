package com.talhaatif.tickojet.bottomsheets


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.talhaatif.tickojet.adapter.CityAdapter
import com.talhaatif.tickojet.databinding.BottomSheetCityBinding

class CityBottomSheet : BottomSheetDialogFragment() {

    interface CitySelectionListener {
        fun onCitySelected(city: String)
    }

    private var listener: CitySelectionListener? = null
    private val cities = listOf(
        "Lahore",
        "Karachi",
        "Islamabad",
        "Rawalpindi",
        "Faisalabad",
        "Multan",
        "Peshawar",
        "Quetta",
        "Gujranwala",
        "Sialkot"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = BottomSheetCityBinding.inflate(inflater, container, false)

        val adapter = CityAdapter(cities) { city ->
            listener?.onCitySelected(city)
            dismiss()
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        return binding.root
    }

    fun setSelectionListener(listener: CitySelectionListener) {
        this.listener = listener
    }
}