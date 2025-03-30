package com.talhaatif.tickojet.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.talhaatif.tickojet.databinding.ItemCurrencyBinding

class CurrencyAdapter(
    private var currencies: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<CurrencyAdapter.ViewHolder>(), Filterable {

    private var filteredList = currencies.toList()

    inner class ViewHolder(val binding: ItemCurrencyBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCurrencyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.currencyName.text = filteredList[position]
        holder.itemView.setOnClickListener {
            onItemClick(filteredList[position])
        }
    }

    override fun getItemCount() = filteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                filteredList = if (constraint.isNullOrEmpty()) {
                    currencies
                } else {
                    currencies.filter {
                        it.contains(constraint.toString(), ignoreCase = true)
                    }
                }
                results.values = filteredList
                results.count = filteredList.size
                return results
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as? List<String> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }
}