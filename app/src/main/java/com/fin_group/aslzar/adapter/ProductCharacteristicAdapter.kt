package com.fin_group.aslzar.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.R
import com.fin_group.aslzar.response.Type
import com.fin_group.aslzar.util.OnProductCharacteristicClickListener
import com.fin_group.aslzar.util.formatNumber

class ProductCharacteristicAdapter(
    var productList: List<Type>,
    private val listener: OnProductCharacteristicClickListener
) : RecyclerView.Adapter<ProductCharacteristicAdapter.ViewHolder>() {

    var selectedItemPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_characteristic_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]
        val isSelected = position == selectedItemPosition
        holder.bind(product, isSelected)

        holder.itemView.setOnClickListener {
            selectedItemPosition = position
            listener.clickCharacteristic(product)
            listener.showProductDialog(product)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<Type>) {
        productList = newList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSelectedPosition(position: Int) {
        selectedItemPosition = position
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvSize: TextView = itemView.findViewById(R.id.tv_size)
        private val tvWeight: TextView = itemView.findViewById(R.id.tv_weight)
        private val tvPrice: TextView = itemView.findViewById(R.id.tv_price)
        private val tvOtherFilial: TextView = itemView.findViewById(R.id.tv_other_filial)

        @SuppressLint("SetTextI18n")
        fun bind(product: Type, isSelected: Boolean) {
            if (product.counts.isNotEmpty()) {
                itemView.visibility = View.VISIBLE
                tvSize.text = formatNumber(product.size)
                tvWeight.text = "${formatNumber(product.weight)} гр"

                val countsList = product.counts
                if (countsList.size == 1){
                    val price = itemView.findViewById<TextView>(R.id.price)
                    price.text = "Цена:"
                }
//                val priceValue = if (product.counts.firstOrNull()?.is_filial == true) {
//                    product.counts.filter { it.is_filial }.minByOrNull { it.price.toDouble() }?.price?.toDouble() ?: 0.0
//                } else {
//                    product.counts.first().price.toDouble()
//                }

                val priceValue = if (product.counts.any { it.is_filial }){
                    product.counts.find { it.is_filial }
                } else {
                    product.counts.minBy { it.price.toDouble() }
                }
                tvPrice.text = "${formatNumber(priceValue!!.price)} UZS"

                if (isSelected) {
                    itemView.setBackgroundResource(R.drawable.selected_item_background_2)
                } else {
                    itemView.setBackgroundResource(R.drawable.selected_item_background_3)
                }
                tvOtherFilial.text = product.counts.size.toString()
            } else {
                itemView.visibility = View.VISIBLE
                itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
            }
        }
    }
}