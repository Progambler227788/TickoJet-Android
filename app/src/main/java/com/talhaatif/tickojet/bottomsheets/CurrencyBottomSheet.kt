package com.talhaatif.tickojet.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.talhaatif.tickojet.adapter.CurrencyAdapter
import com.talhaatif.tickojet.databinding.BottomSheetCurrencyBinding

class CurrencyBottomSheet : BottomSheetDialogFragment() {

    interface CurrencySelectionListener {
        fun onCurrencySelected(currency: String)
    }

    private var listener: CurrencySelectionListener? = null
    private val currencies = listOf(
        "USD - US Dollar",
        "EUR - Euro",
        "GBP - British Pound",
        "PKR - Pakistani Rupee",
        "INR - Indian Rupee",
        "AED - UAE Dirham"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = BottomSheetCurrencyBinding.inflate(inflater, container, false)

        // Setup RecyclerView
        val adapter = CurrencyAdapter(currencies) { currency ->
            listener?.onCurrencySelected(currency.split(" ")[0])
            dismiss()
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }

        // Setup search
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        return binding.root
    }

    fun setSelectionListener(listener: CurrencySelectionListener) {
        this.listener = listener
    }
}