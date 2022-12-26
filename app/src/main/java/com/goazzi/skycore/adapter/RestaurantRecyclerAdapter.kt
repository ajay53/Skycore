package com.goazzi.skycore.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.goazzi.skycore.BR
import com.goazzi.skycore.databinding.RestaurantListItemBinding
import com.goazzi.skycore.model.Business

class RestaurantRecyclerAdapter(
    private val context: Context,
    private val businessList: List<Business>,
    private val onRestaurantClickListener: OnRestaurantClickListener
) : RecyclerView.Adapter<RestaurantRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater =
            LayoutInflater.from(parent.context)
        val itemBinding: RestaurantListItemBinding = RestaurantListItemBinding.inflate(
            layoutInflater,
            parent,
            false
        )
        return ViewHolder(itemBinding, onRestaurantClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: Business = businessList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return businessList.size
    }

    class ViewHolder(
        binding: RestaurantListItemBinding,
        private val onRestaurantClickListener: OnRestaurantClickListener
    ) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        private val binding: RestaurantListItemBinding

        init {
            this.binding = binding
            this.binding.root.setOnClickListener(this)
        }

        fun bind(business: Business) {
            binding.setVariable(BR.item, business)
            binding.executePendingBindings()
        }

        override fun onClick(p0: View?) {
            onRestaurantClickListener.onRestaurantClick(adapterPosition)
        }
    }

    interface OnRestaurantClickListener {
        fun onRestaurantClick(pos: Int)
    }
}