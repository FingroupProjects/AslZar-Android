package com.fin_group.aslzar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.R
import com.fin_group.aslzar.models.ImageDataModel
import com.fin_group.aslzar.util.OnImageClickListener

class BottomSheetItemAdapter(private var mList:ArrayList<ImageDataModel>, private val listener: OnImageClickListener): RecyclerView.Adapter<BottomSheetItemAdapter.ViewHolder>() {

    var selectedItemPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        val item = mList[position]
        //holder.item.text = item
        val isSelected = position == selectedItemPosition

        holder.bindItems(item, isSelected)


        holder.itemView.setOnClickListener {
            selectedItemPosition = position
            notifyDataSetChanged()
            listener.setImage(item.image)
        }

    }

    class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView){
        val item: TextView = ItemView.findViewById(R.id.tvItem)

        fun bindItems(imageDataModel: ImageDataModel, isSelected: Boolean) {
            val imageView = itemView.findViewById<ImageView>(R.id.tvItemm)
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