package com.fin_group.aslzar.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fin_group.aslzar.databinding.RowSliderItem2Binding
import com.fin_group.aslzar.models.ImageDataModel2
import com.fin_group.aslzar.util.OnAlikeProductClickListener

class AlikeProductsAdapter(
    var alikeProductsList: List<ImageDataModel2>,
    val listener: OnAlikeProductClickListener
) : RecyclerView.Adapter<AlikeProductsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowSliderItem2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun getItemCount() = alikeProductsList.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alikeProduct = alikeProductsList[position]
        holder.bindItems(alikeProduct)

        holder.itemView.setOnClickListener {
            listener.callBottomDialog(alikeProduct.id)
        }
    }
    inner class ViewHolder(binding: RowSliderItem2Binding) : RecyclerView.ViewHolder(binding.root) {
        val imageView = binding.imageView
        @SuppressLint("CheckResult")
        fun bindItems(likeProduct: ImageDataModel2) {
            Glide.with(itemView.context).load(likeProduct.image).override(180, 180).into(imageView)

//            imageView.setImageResource(likeProduct.image)
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<ImageDataModel2>) {
        alikeProductsList = newList
        notifyDataSetChanged()
    }
}