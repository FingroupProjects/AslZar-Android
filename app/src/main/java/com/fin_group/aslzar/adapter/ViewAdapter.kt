package com.fin_group.aslzar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fin_group.aslzar.R
import com.fin_group.aslzar.models.ImageDataModel

class ViewAdapter(private val imageDataModelList: ArrayList<ImageDataModel>) : RecyclerView.Adapter<ViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_slider_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(imageDataModelList[position])
    }

    override fun getItemCount(): Int {
        return imageDataModelList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(imageDataModel: ImageDataModel) {
            val textView = itemView.findViewById<TextView>(R.id.tvName)
            val imageView = itemView.findViewById<ImageView>(R.id.imageView)
            textView.text = imageDataModel.name

            Glide.with(itemView.context).load(imageDataModel.image).into(imageView)
        }
    }
}