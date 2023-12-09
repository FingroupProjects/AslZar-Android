package com.fin_group.aslzar.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fin_group.aslzar.R
import com.fin_group.aslzar.databinding.RowSliderItem2Binding
import com.fin_group.aslzar.response.SimilarProduct
import com.fin_group.aslzar.util.OnAlikeProductClickListener

class AlikeProductsAdapter(
    var alikeProductsList: List<SimilarProduct>,
    val listener: OnAlikeProductClickListener
) : RecyclerView.Adapter<AlikeProductsAdapter.ViewHolder>() {

    var selectedItemPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowSliderItem2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun getItemCount() = alikeProductsList.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alikeProduct = alikeProductsList[position]
        val isSelected = position == selectedItemPosition
        holder.bindItems(alikeProduct, isSelected)
        holder.itemView.setOnClickListener {
            listener.callBottomDialog(alikeProduct)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSelectedPositions(position: Int) {
        selectedItemPosition = position
        notifyDataSetChanged()
    }


    inner class ViewHolder(binding: RowSliderItem2Binding) : RecyclerView.ViewHolder(binding.root) {
        val imageView = binding.imageView
        @SuppressLint("CheckResult")
        fun bindItems(likeProduct: SimilarProduct, isSelected: Boolean) {
            if (isSelected) {
                itemView.setBackgroundResource(R.drawable.selected_item_background)
            } else {
                itemView.background = null
            }
//            Glide.with(itemView.context).load(likeProduct.img[0]).override(180, 180).into(imageView)
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<SimilarProduct>) {
        alikeProductsList = newList
        notifyDataSetChanged()
    }
}