package com.fin_group.aslzar.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fin_group.aslzar.R
import com.fin_group.aslzar.databinding.ListItemBinding
import com.fin_group.aslzar.response.ResultXV2
import com.fin_group.aslzar.util.OnProductClickListener

class SetInProductAdapter(
    private var productList: List<ResultXV2>,
    private val listener: OnProductClickListener
) : RecyclerView.Adapter<SetInProductAdapter.ViewHolder>() {

    private var selectedItemPosition = RecyclerView.NO_POSITION
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

    fun getProductByPosition(position: Int): ResultXV2 {
        return productList[position]
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<ResultXV2>) {
        productList = newList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSelectedPositions(position: Int) {
        selectedItemPosition = position
        notifyDataSetChanged()
    }

    inner class ViewHolder(binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindSetProductItems(imageDataModel: ResultXV2, isSelected: Boolean) {
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