package com.fin_group.aslzar.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.databinding.RowItemHaveingInStoreBinding
import com.fin_group.aslzar.databinding.RowItemTableInstallmentBinding
import com.fin_group.aslzar.response.Percent
import com.fin_group.aslzar.response.PercentInstallment
import com.fin_group.aslzar.util.formatNumber


class TableInstallmentAdapter(private val installment: PercentInstallment, val totalPrice: Number):  RecyclerView.Adapter<TableInstallmentAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: RowItemTableInstallmentBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(percent: Percent){
            binding.apply {
                countPayment.text = "${percent.mounth} платежей (${percent.coefficient}%)"
                val monthPayment = (((totalPrice.toDouble() * percent.coefficient.toDouble()) / 100) + totalPrice.toDouble()) / percent.mounth.toDouble()
                sizePayment.text = formatNumber(monthPayment)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowItemTableInstallmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return installment.result.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val percent = installment.result[position]
        holder.bind(percent)
    }

}