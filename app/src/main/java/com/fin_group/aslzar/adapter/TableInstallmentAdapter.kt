package com.fin_group.aslzar.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.R
import com.fin_group.aslzar.databinding.RowItemHaveingInStoreBinding
import com.fin_group.aslzar.databinding.RowItemTableInstallmentBinding
import com.fin_group.aslzar.response.Percent
import com.fin_group.aslzar.response.PercentInstallment
import com.fin_group.aslzar.util.formatNumber


class TableInstallmentAdapter(var installment: PercentInstallment, var totalPrice: Number):  RecyclerView.Adapter<TableInstallmentAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: RowItemTableInstallmentBinding): RecyclerView.ViewHolder(binding.root) {
        val tvMonth = binding.countPayment
        val tvPercent = binding.sizePayment
        @SuppressLint("SetTextI18n")
        fun bind(percent: Percent){
            binding.apply {
                countPayment.text = "${percent.mounth} платежей"
                val monthPayment = (((totalPrice.toDouble() * percent.coefficient.toDouble()) / 100) + totalPrice.toDouble()) / percent.mounth.toDouble()
                sizePayment.text = "${formatNumber(monthPayment)} UZS"
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newPercentInstallment: PercentInstallment, newTotalPrice: Number) {
        installment = newPercentInstallment
        totalPrice = newTotalPrice
        notifyDataSetChanged()
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

        val lastItem = position == itemCount - 1

        if (lastItem){
            holder.tvMonth.setBackgroundResource(R.drawable.bg_text_view_in_table_last_2)
            holder.tvPercent.setBackgroundResource(R.drawable.bg_text_view_in_table_last_1)
        } else {
            holder.tvMonth.setBackgroundResource(R.drawable.bg_text_view_in_table)
            holder.tvPercent.setBackgroundResource(R.drawable.bg_text_view_in_table)
        }
    }

}