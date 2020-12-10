package com.ngmatt.weedmapsandroidcodechallenge.ui.businesses

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ngmatt.weedmapsandroidcodechallenge.data.Business
import com.ngmatt.weedmapsandroidcodechallenge.databinding.ItemBusinessBinding

class BusinessesAdapter : ListAdapter<Business, BusinessesAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemBusinessBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val business = getItem(position)
        holder.bind(business)
    }

    class ViewHolder(private val binding: ItemBusinessBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(business: Business) {
            binding.business = business
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Business>() {
        override fun areItemsTheSame(oldItem: Business, newItem: Business) = oldItem === newItem

        override fun areContentsTheSame(oldItem: Business, newItem: Business) = oldItem.id == newItem.id
    }
}