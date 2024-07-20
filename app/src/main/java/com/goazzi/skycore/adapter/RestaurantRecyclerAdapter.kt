package com.goazzi.skycore.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.goazzi.skycore.BR
import com.goazzi.skycore.databinding.LayoutRestaurantListItemBinding
import com.goazzi.skycore.model.Business

class RestaurantRecyclerAdapter(
    private val context: Context,
    private val businessList: MutableList<Business>,
    private val onRestaurantClickListener: OnRestaurantClickListener
) : RecyclerView.Adapter<RestaurantRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d(TAG, "onCreateViewHolder: ")
        val layoutInflater =
            LayoutInflater.from(parent.context)
        val itemBinding: LayoutRestaurantListItemBinding = LayoutRestaurantListItemBinding.inflate(
            layoutInflater,
            parent,
            false
        )
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: Business = businessList[position]
        Log.d(TAG, "onBindViewHolder: $position : $item")
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return businessList.size
    }

    fun refreshList(restaurants: List<Business>) {
        val oldList = businessList
        val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(
            RestaurantDiffCallback(oldList, restaurants)
        )
        businessList.clear()
        businessList.addAll(restaurants)

        diffResult.dispatchUpdatesTo(this)
    }

    inner class RestaurantDiffCallback(
        private val oldList: List<Business>,
        private val newList: List<Business>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            //No need to compare each property individually, Data class automatically derives the equals() and hashCode()
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

    inner class ViewHolder(
        binding: LayoutRestaurantListItemBinding,
    ) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        private val binding: LayoutRestaurantListItemBinding

        init {
            this.binding = binding
            this.binding.root.setOnClickListener(this)
        }

        fun bind(business: Business) {
            binding.setVariable(BR.item, business)
            binding.executePendingBindings()
        }

        override fun onClick(p0: View?) {
            onRestaurantClickListener.onRestaurantClick(absoluteAdapterPosition)
        }
    }

    interface OnRestaurantClickListener {
        fun onRestaurantClick(pos: Int)
    }

    companion object {
        private const val TAG = "RestaurantRecyclerAdapt"
    }
}