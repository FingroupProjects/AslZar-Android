package com.fin_group.aslzar.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.R
import com.fin_group.aslzar.databinding.RowItemTableInstallmentBinding
import com.fin_group.aslzar.response.Percent
import com.fin_group.aslzar.response.PercentInstallment
import com.fin_group.aslzar.util.formatNumber
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class TableInstallmentAdapter(var installment: PercentInstallment, var totalPrice: Number, var limit: Number):  RecyclerView.Adapter<TableInstallmentAdapter.ViewHolder>() {

    private lateinit var context: Context

    inner class ViewHolder(val binding: RowItemTableInstallmentBinding): RecyclerView.ViewHolder(binding.root) {
        val tvMonth = binding.countPayment
        val tvPercent = binding.sizePayment
        @SuppressLint("SetTextI18n", "ResourceAsColor")
        fun bind(percent: Percent){
            tvMonth.text = "${percent.mounth} платежей"
            val monthPayment = if (percent.coefficient.toDouble() <= 1.0) {
                totalPrice.toDouble()
            } else {
                (((totalPrice.toDouble() * percent.coefficient.toDouble()) / 100) + totalPrice.toDouble()) / percent.mounth.toDouble()
            }

            if (limit.toDouble() >= monthPayment || limit.toDouble() == 0.0) {
                tvPercent.setTextColor(ContextCompat.getColor(tvPercent.context, R.color.text_color_1))
            } else {
                tvPercent.setTextColor(ContextCompat.getColor(tvPercent.context, R.color.background_7))
            }
            tvPercent.text = "${formatNumber(monthPayment)} UZS"
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newPercentInstallment: PercentInstallment, newTotalPrice: Number) {
        installment = newPercentInstallment
        totalPrice = newTotalPrice
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding = RowItemTableInstallmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return installment.result.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val percent = installment.result[position]
        holder.bind(percent)

        val monthPayment = (((totalPrice.toDouble() * percent.coefficient.toDouble()) / 100) + totalPrice.toDouble()) / percent.mounth.toDouble()

        if (!(limit.toDouble() >= monthPayment || limit.toDouble() == 0.0)) {
            holder.tvPercent.setOnClickListener {
                val dialog = MaterialAlertDialogBuilder(context)
                dialog.setTitle("Превышен лимит")
                dialog.setMessage("Данный клиент не сможет выплатить ${formatNumber(monthPayment)} UZS в месяц, его лимит:${formatNumber(limit)} UZS в месяц")
                val positiveButton = dialog.setPositiveButton("ок", null).show().getButton(DialogInterface.BUTTON_POSITIVE)
                positiveButton.setTextColor(ContextCompat.getColor(context, R.color.background_2))
            }
        }

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