package com.fin_group.aslzar.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.R
import com.fin_group.aslzar.databinding.RowItemCharacterProductBinding
import com.fin_group.aslzar.response.Count
import com.fin_group.aslzar.response.ResultX
import com.fin_group.aslzar.response.Type
import com.fin_group.aslzar.util.AddingProduct
import com.fin_group.aslzar.util.FilialListener
import com.fin_group.aslzar.util.formatNumber

class ProductsInAddAdapter(
    private var product: ResultX,
    private var typeProductList: List<Type>,
    private var listener: AddingProduct,
    val listener2: FilialListener
) : RecyclerView.Adapter<ProductsInAddAdapter.ViewHolder>() {

    lateinit var secondAdapter: FilialAdapter
    private lateinit var context: Context

    inner class ViewHolder(binding: RowItemCharacterProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val code = binding.characterCode
        val price = binding.characterPrice
        val weight = binding.characterWeight
        val size = binding.characterSize
        val provider = binding.characterProvider
        val madeIn = binding.characterMadeIn
        val filialList = binding.rvFilials

        val mainLayout = binding.mainLayout
        val firstLayout = binding.firstProductLayout
        val secondLayout = binding.secondProductLayout
        val showGoods = binding.showGoodsBtn

        fun bind(type: Type) {
            code.text = type.name
            size.text = type.size.toString()
            weight.text = type.weight.toString()
            provider.text = type.provider
            madeIn.text = type.country_of_origin
            val minFilial = type.counts
                .filter { it.is_filial }
                .minByOrNull { it.price.toDouble() }
                ?: type.counts.minByOrNull { it.price.toDouble() }

            price.text = formatNumber(minFilial?.price?.toDouble() ?: 0.0)

            val isExpandable = type.isExpandable
            secondLayout.visibility = if (isExpandable) VISIBLE else GONE
            showGoods.setImageResource(if (type.counts.size == 1) R.drawable.ic_add_2 else if (isExpandable) R.drawable.ic_less else R.drawable.ic_drop_down)

            filialList.layoutManager = LinearLayoutManager(context)
            secondAdapter = FilialAdapter(product, type, type.counts, listener2)
            filialList.adapter = secondAdapter
        }
    }

    fun upgradeList(newTypeList: List<Type>){
        typeProductList = newTypeList
        notifyDataSetChanged()
    }

    fun updateFilial(newCountList: List<Count>){
        secondAdapter.updateFilial(newCountList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding = RowItemCharacterProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return typeProductList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val type = typeProductList[position]
        holder.bind(type)

        holder.showGoods.setOnClickListener {
            when (type.counts.size) {
                1 -> {
                    listener.addProduct(product, type, type.counts[0])
                }
                else -> {
                    isAnyItemExpand(position)
                    type.isExpandable = !type.isExpandable
                    notifyItemChanged(position)
                }
            }
        }

        holder.firstLayout.setOnClickListener {
            isAnyItemExpand(position)
            type.isExpandable = !type.isExpandable
            notifyItemChanged(position)
        }
    }


    private fun isAnyItemExpand(position: Int) {
        val temp = typeProductList.indexOfFirst { it.isExpandable }
        if (temp >= 0 && temp != position) {
            typeProductList[temp].isExpandable = false
            notifyItemChanged(temp)
        }
    }
}