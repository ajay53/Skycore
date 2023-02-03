package com.goazzi.skycore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.goazzi.skycore.BR
import com.goazzi.skycore.databinding.LayoutRestaurantListItemBinding
import com.goazzi.skycore.model.Business

class RestaurantRecyclerAdapterNew(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Business>() {

        override fun areItemsTheSame(
            oldItem: Business,
            newItem: Business
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Business,
            newItem: Business
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        /*return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_restaurant_list_item,
                parent,
                false
            ),
            interaction
        )*/


        val layoutInflater =
            LayoutInflater.from(parent.context)
        val itemBinding: LayoutRestaurantListItemBinding = LayoutRestaurantListItemBinding.inflate(
            layoutInflater,
            parent,
            false
        )
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Business>) {
        differ.submitList(list)
    }

    fun removeAt(position: Int) {
        val list:MutableList<Business> = mutableListOf<Business>().apply { addAll(differ.currentList) }
        list.removeAt(position)
        submitList(list)
    }

    fun insertAdd(position: Int){

    }

    inner class ViewHolder(binding: LayoutRestaurantListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val binding: LayoutRestaurantListItemBinding

        init {
            this.binding = binding
        }

        fun bind(item: Business) {
            binding.setVariable(BR.item, item)
            binding.executePendingBindings()

            itemView.setOnClickListener {
                interaction?.onItemSelected(absoluteAdapterPosition, item)
            }
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Business)
    }
}