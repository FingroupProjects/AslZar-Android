package com.fin_group.aslzar.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.databinding.RowItemFilialCharacterBinding
import com.fin_group.aslzar.response.Count
import com.fin_group.aslzar.response.ResultX
import com.fin_group.aslzar.response.Type
import com.fin_group.aslzar.util.FilialListener
import com.fin_group.aslzar.util.formatNumber

class FilialAdapter(private val product: ResultX, private val type: Type, private val filialList: List<Count>, private val listener: FilialListener): RecyclerView.Adapter<FilialAdapter.ViewHolder>() {
    inner class ViewHolder(binding: RowItemFilialCharacterBinding): RecyclerView.ViewHolder(binding.root) {
        val filialName = binding.branchName
        val showCase = binding.branchShowcase
        val price = binding.branchPrice
        val addBtn = binding.branchAddToCart

        fun bind(filial: Count){
            filialName.text = filial.filial
            showCase.text = filial.sclad
            price.text = formatNumber(filial.price)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowItemFilialCharacterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return filialList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val filial = filialList[position]
        holder.bind(filial)

        holder.addBtn.setOnClickListener {
            listener.addFilial(product, type, filial)
        }
    }
}