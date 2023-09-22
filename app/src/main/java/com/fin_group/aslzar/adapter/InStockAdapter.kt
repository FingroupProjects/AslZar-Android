package com.fin_group.aslzar.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.databinding.RowItemHaveingInStoreBinding
import com.fin_group.aslzar.models.InStockProduct
import com.fin_group.aslzar.response.InStock

class InStockAdapter(private val inStockList: List<InStock>): RecyclerView.Adapter<InStockAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: RowItemHaveingInStoreBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(inStock: InStock){
            binding.apply {
                store.text = inStock.subsidiary
                showCase.text = inStock.store_house
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