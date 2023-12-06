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

class ProductCharacteristicAdapter( var productList: List<Type>, private val listener: OnProductCharacteristicClickListener) :
    RecyclerView.Adapter<ProductCharacteristicAdapter.ViewHolder>() {

    var selectedItemPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_characteristic_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]
        val isSelected = position == selectedItemPosition
        holder.bind(product, isSelected)

        holder.itemView.setOnClickListener {
            selectedItemPosition = position
            listener.clickCharacteristic(product)
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

        fun bind(product: Type, isSelected: Boolean) {
            if (product.counts.isNotEmpty()) {
                itemView.visibility = View.VISIBLE
                tvSize.text = product.size.toString()
                tvWeight.text = product.weight.toString()
                val priceValue = product.counts.firstOrNull()?.price ?: 0
                tvPrice.text = priceValue.toString()


                if (isSelected){
                    itemView.setBackgroundResource(R.drawable.selected_item_background_2)
                }else{
                    itemView.setBackgroundResource(R.drawable.selected_item_background_3)
                }

                tvOtherFilial.text = product.counts.size.toString()
            } else {
                itemView.visibility = View.GONE
                itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
            }
        }
    }
}