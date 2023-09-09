package com.fin_group.aslzar.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fin_group.aslzar.R
import com.fin_group.aslzar.databinding.ListItemBinding
import com.fin_group.aslzar.response.Product
import com.fin_group.aslzar.util.OnProductClickListener

class SetInProductAdapter(
    private var productList: List<Product>,
    private val listener: OnProductClickListener
) : RecyclerView.Adapter<SetInProductAdapter.ViewHolder>() {

    var selectedItemPosition = RecyclerView.NO_POSITION
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = productList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = productList[position]
        val isSelected = position == selectedItemPosition
        holder.bindSetProductItems(item, isSelected)
        holder.itemView.setOnClickListener {
            selectedItemPosition = position
            listener.setProduct(item)
        }
    }

    fun getProductByPosition(position: Int): Product {
        return productList[position]
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Product>) {
        productList = newList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSelectedPositions(position: Int) {
        selectedItemPosition = position
        notifyDataSetChanged()
    }

    inner class ViewHolder(binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindSetProductItems(imageDataModel: Product, isSelected: Boolean) {
            val imageView = itemView.findViewById<ImageView>(R.id.image)
//            val textView = itemView.findViewById<TextView>(R.id.tvName)
//            textView.text = imageDataModel.name
            if (isSelected) {
                itemView.setBackgroundResource(R.drawable.selected_item_background)
            } else {
                itemView.background = null
            }
            Glide.with(itemView.context).load(imageDataModel.img[0]).into(imageView)
        }
    }
}