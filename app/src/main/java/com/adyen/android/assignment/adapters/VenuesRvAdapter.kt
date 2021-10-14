package com.adyen.android.assignment.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.adyen.android.assignment.databinding.VenueItemBinding
import com.adyen.android.assignment.utils.ClickListener
import java.util.*

class VenuesRvAdapter(private val venueList: MutableList<VenueItem>, private val listener: ClickListener<VenueItem>):
    RecyclerView.Adapter<VenuesRvAdapter.VenuesViewHolder>(), Filterable {
    /** SET UP VENUE SEARCH */
    var venueFilterList = mutableListOf<VenueItem>()
    init {
        venueFilterList = venueList
    }

    inner class VenuesViewHolder(private val binding: VenueItemBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(venue: VenueItem, action: ClickListener<VenueItem>) {
            binding.apply {
                venueNameTv.text = venue.name.toUpperCase()
                venueDistanceTv.text = "${venue.distance} m"
                venueLocationTv.text = "${venue.latitude}, ${venue.longitude}"
                venueAddressTv.text = "${venue.address.trim()}"
            }
            itemView.setOnClickListener {
                action.onItemClick(venue, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VenuesViewHolder {
        val binding = VenueItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VenuesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VenuesViewHolder, position: Int) {
        val currentItem = venueFilterList[position]
        holder.bind(currentItem, listener)
    }

    override fun getItemCount(): Int {
        return venueFilterList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val charSearch = p0.toString()
                venueFilterList = if(charSearch.isEmpty()) {
                    venueList
                } else {
                    val result = mutableListOf<VenueItem>()
                    for(venue in venueList) {
                        if(venue.name.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            result.add(venue)
                        }
                    }
                    result
                }
                val filterResults = FilterResults()
                filterResults.values = venueFilterList
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                venueFilterList = p1?.values as MutableList<VenueItem>
                notifyDataSetChanged()
            }
        }
    }
}