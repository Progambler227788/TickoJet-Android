package com.talhaatif.tickojet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.talhaatif.tickojet.databinding.ItemCurrencyBinding

class CityAdapter(
    private var cities: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<CityAdapter.CityViewHolder>(), Filterable {

    private var filteredList: List<String> = cities

    inner class CityViewHolder(private val binding: ItemCurrencyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(city: String) {
            binding.currencyName.text = city
            binding.root.setOnClickListener { onItemClick(city) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val binding = ItemCurrencyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    override fun getItemCount() = filteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filtered = if (constraint.isNullOrEmpty()) {
                    cities
                } else {
                    cities.filter {
                        it.contains(constraint, ignoreCase = true)
                    }
                }
                return FilterResults().apply { values = filtered }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as? List<String> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }
}