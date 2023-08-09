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
import com.fin_group.aslzar.util.OnImageClickListener

class ViewAdapter(val imageDataModelList: ArrayList<ImageDataModel>, private val listener: OnImageClickListener) : RecyclerView.Adapter<ViewAdapter.ViewHolder>() {

    var selectedItemPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_slider_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = imageDataModelList[position]
        val isSelected = position == selectedItemPosition

        holder.bindItems(product, isSelected)


        holder.itemView.setOnClickListener {
            selectedItemPosition = position
            notifyDataSetChanged()
            listener.setImage(product.image)
        }
    }

    override fun getItemCount(): Int {
        return imageDataModelList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(imageDataModel: ImageDataModel, isSelected: Boolean) {
            val imageView = itemView.findViewById<ImageView>(R.id.imageView)
//            val textView = itemView.findViewById<TextView>(R.id.tvName)
//            textView.text = imageDataModel.name
//            Glide.with(itemView.context).load(imageDataModel.image).into(imageView)
            if (isSelected) {
                itemView.setBackgroundResource(R.drawable.selected_item_background)
            } else {
                itemView.background = null
            }
            imageView.setImageResource(imageDataModel.image)
        }
    }
}