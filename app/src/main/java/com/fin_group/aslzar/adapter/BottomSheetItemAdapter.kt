package com.fin_group.aslzar.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.R
import com.fin_group.aslzar.models.ImageDataModel
import com.fin_group.aslzar.util.OnImageClickListener

class BottomSheetItemAdapter(var mList: List<ImageDataModel>, private val listener: OnImageClickListener)
    : RecyclerView.Adapter<BottomSheetItemAdapter.ViewHolder>() {

    var selectedItemPosition = RecyclerView.NO_POSITION
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int {
        return mList.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]
        val isSelected = position == selectedItemPosition
        holder.bindSetProductItems(item, isSelected)
        holder.itemView.setOnClickListener {
            selectedItemPosition = position
            listener.setImage(item.image)
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<ImageDataModel>) {
        mList = newList
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setSelectedPositions(position: Int) {
        selectedItemPosition = position
        notifyDataSetChanged()
    }
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        fun bindSetProductItems(imageDataModel: ImageDataModel, isSelected: Boolean) {
            val imageView = itemView.findViewById<ImageView>(R.id.image)
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