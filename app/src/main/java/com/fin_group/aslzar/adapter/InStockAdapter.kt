package com.fin_group.aslzar.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.databinding.RowItemHaveingInStoreBinding
import com.fin_group.aslzar.models.InStockProduct

class InStockAdapter(private val inStockList: List<InStockProduct>): RecyclerView.Adapter<InStockAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: RowItemHaveingInStoreBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(inStock: InStockProduct){
            binding.apply {
                store.text = inStock.store
                showCase.text = inStock.showcase
                count.text = inStock.count.toString()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowItemHaveingInStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return inStockList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val inStock = inStockList[position]
        holder.bind(inStock)
    }
}